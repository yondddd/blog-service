package com.yond.blog.util.comment;

import com.yond.blog.cache.remote.QQAvatarCache;
import com.yond.blog.config.properties.BlogProperties;
import com.yond.blog.entity.CommentDO;
import com.yond.blog.entity.UserDO;
import com.yond.blog.service.AboutService;
import com.yond.blog.service.BlogService;
import com.yond.blog.service.FriendService;
import com.yond.blog.service.UserService;
import com.yond.blog.util.HashUtils;
import com.yond.blog.util.IpAddressUtils;
import com.yond.blog.util.MailUtils;
import com.yond.blog.util.QQInfoUtils;
import com.yond.blog.util.comment.channel.ChannelFactory;
import com.yond.blog.util.comment.channel.CommentNotifyChannel;
import com.yond.blog.web.blog.view.dto.Comment;
import com.yond.blog.web.blog.view.vo.FriendInfo;
import com.yond.common.constant.PageConstant;
import com.yond.common.enums.CommentOpenStateEnum;
import com.yond.common.enums.CommentPageEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 评论工具类
 *
 * @author: Naccl
 * @date: 2022-01-22
 */
@Component
@DependsOn("springContextUtils")
public class CommentUtils {
    @Autowired
    private BlogProperties blogProperties;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private AboutService aboutService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;
    @Autowired
    private QQAvatarCache qqAvatarCache;

    private static BlogService blogService;

    private CommentNotifyChannel notifyChannel;
    /**
     * 新评论是否默认公开
     */
    private Boolean commentDefaultOpen;

    @Autowired
    public void setBlogService(BlogService blogService) {
        CommentUtils.blogService = blogService;
    }

    @Value("${comment.notify.channel}")
    public void setNotifyChannel(String channelName) {
        this.notifyChannel = ChannelFactory.getChannel(channelName);
    }

    @Value("${comment.default-open}")
    public void setCommentDefaultOpen(Boolean commentDefaultOpen) {
        this.commentDefaultOpen = commentDefaultOpen;
    }

    /**
     * 判断是否发送提醒
     * 6种情况：
     * 1.我以父评论提交：不用提醒
     * 2.我回复我自己：不用提醒
     * 3.我回复访客的评论：只提醒该访客
     * 4.访客以父评论提交：只提醒我自己
     * 5.访客回复我的评论：只提醒我自己
     * 6.访客回复访客的评论(即使是他自己先前的评论)：提醒我自己和他回复的评论
     *
     * @param comment          当前收到的评论
     * @param isVisitorComment 是否访客评论
     * @param parentComment    父评论
     */
    public void judgeSendNotify(Comment comment, boolean isVisitorComment, CommentDO parentComment) {
        if (parentComment != null && !parentComment.getAdminComment() && parentComment.getNotice()) {
            //我回复访客的评论，且对方接收提醒，邮件提醒对方(3)
            //访客回复访客的评论(即使是他自己先前的评论)，且对方接收提醒，邮件提醒对方(6)
            sendMailToParentComment(parentComment, comment);
        }
        if (isVisitorComment) {
            //访客以父评论提交，只提醒我自己(4)
            //访客回复我的评论，提醒我自己(5)
            //访客回复访客的评论，不管对方是否接收提醒，都要提醒我有新评论(6)
            notifyMyself(comment);
        }
    }

    /**
     * 发送邮件提醒回复对象
     *
     * @param parentComment 父评论
     * @param comment       当前收到的评论
     */
    private void sendMailToParentComment(CommentDO parentComment, Comment comment) {
        CommentPageEnum commentPageEnum = getCommentPageEnum(comment);
        Map<String, Object> map = new HashMap<>(16);
        map.put("parentNickname", parentComment.getNickname());
        map.put("nickname", comment.getNickname());
        map.put("title", commentPageEnum.getTitle());
        map.put("time", comment.getCreateTime());
        map.put("parentContent", parentComment.getContent());
        map.put("content", comment.getContent());
        map.put("url", blogProperties.getView() + commentPageEnum.getPath());
        String toAccount = parentComment.getEmail();
        String subject = "您在 " + blogProperties.getName() + " 的评论有了新回复";
        mailUtils.sendHtmlTemplateMail(map, toAccount, subject, "guest.html");
    }

    /**
     * 通过指定方式通知自己
     *
     * @param comment 当前收到的评论
     */
    private void notifyMyself(Comment comment) {
        notifyChannel.notifyMyself(comment);
    }

    /**
     * 获取评论对应的页面
     *
     * @param comment 当前收到的评论
     * @return CommentPageEnum
     */
    public static CommentPageEnum getCommentPageEnum(Comment comment) {
        CommentPageEnum commentPageEnum = CommentPageEnum.UNKNOWN;
        switch (comment.getPage()) {
            case 0:
                //普通博客
                commentPageEnum = CommentPageEnum.BLOG;
                commentPageEnum.setTitle(blogService.getTitleByBlogId(comment.getBlogId()));
                commentPageEnum.setPath("/blog/" + comment.getBlogId());
                break;
            case 1:
                //关于我页面
                commentPageEnum = CommentPageEnum.ABOUT;
                break;
            case 2:
                //友链页面
                commentPageEnum = CommentPageEnum.FRIEND;
                break;
            default:
                break;
        }
        return commentPageEnum;
    }

    /**
     * 查询对应页面评论是否开启
     *
     * @param page   页面分类（0普通文章，1关于我，2友链）
     * @param blogId 如果page==0，需要博客id参数，校验文章是否公开状态
     * @return CommentOpenStateEnum
     */
    public CommentOpenStateEnum judgeCommentState(Integer page, Long blogId) {
        switch (page) {
            case PageConstant.BLOG:
                //普通博客
                Boolean commentEnabled = blogService.getCommentEnabledByBlogId(blogId);
                Boolean published = blogService.getPublishedByBlogId(blogId);
                if (commentEnabled == null || published == null) {
                    //未查询到此博客
                    return CommentOpenStateEnum.NOT_FOUND;
                } else if (!published) {
                    //博客未公开
                    return CommentOpenStateEnum.NOT_FOUND;
                } else if (!commentEnabled) {
                    //博客评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                //判断文章是否存在密码
                String password = blogService.getBlogPassword(blogId);
                if (!StringUtils.isBlank(password)) {
                    return CommentOpenStateEnum.PASSWORD;
                }
                break;
            case PageConstant.ABOUT:
                //关于我页面
                if (!aboutService.getCommentEnabled()) {
                    //页面评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                break;
            case PageConstant.FRIEND:
                //友链页面
                FriendInfo friendInfo = friendService.getFriendInfo(true, false);
                if (!friendInfo.getCommentEnabled()) {
                    //页面评论已关闭
                    return CommentOpenStateEnum.CLOSE;
                }
                break;
            default:
                break;
        }
        return CommentOpenStateEnum.OPEN;
    }

    /**
     * 对于昵称不是QQ号的评论，根据昵称Hash设置头像
     *
     * @param comment 当前收到的评论
     */
    private void setCommentRandomAvatar(Comment comment) {
        //设置随机头像
        //根据评论昵称取Hash，保证每一个昵称对应一个头像
        long nicknameHash = HashUtils.getMurmurHash32(comment.getNickname());
        //计算对应的头像
        long num = nicknameHash % 6 + 1;
        String avatar = "/img/comment-avatar/" + num + ".jpg";
        comment.setAvatar(avatar);
    }

    /**
     * 通用博主评论属性
     *
     * @param comment 评论DTO
     * @param admin   博主信息
     */
    private void setGeneralAdminComment(Comment comment, UserDO admin) {
        comment.setAdminComment(true);
        comment.setCreateTime(new Date());
        comment.setPublished(true);
        comment.setAvatar(admin.getAvatar());
        comment.setWebsite("/");
        comment.setNickname(admin.getNickname());
        comment.setEmail(admin.getEmail());
        comment.setNotice(false);
    }

    /**
     * 为[Telegram快捷回复]方式设置评论属性
     *
     * @param comment 评论DTO
     */
    public void setAdminCommentByTelegramAction(Comment comment) {
        //查出博主信息，默认id为1的记录就是博主
        UserDO admin = userService.getById(1L);

        setGeneralAdminComment(comment, admin);
        comment.setIp("via Telegram");
    }

    /**
     * 设置博主评论属性
     *
     * @param comment 当前收到的评论
     * @param request 用于获取ip
     * @param admin   博主信息
     */
    public void setAdminComment(Comment comment, HttpServletRequest request, UserDO admin) {
        setGeneralAdminComment(comment, admin);
        comment.setIp(IpAddressUtils.getIpAddress(request));
    }

    /**
     * 设置访客评论属性
     *
     * @param comment 当前收到的评论
     * @param request 用于获取ip
     */
    public void setVisitorComment(Comment comment, HttpServletRequest request) {
        String nickname = comment.getNickname();
        try {
            if (QQInfoUtils.isQQNumber(nickname)) {
                comment.setQq(nickname);
                comment.setNickname(QQInfoUtils.getQQNickname(nickname));
                setCommentQQAvatar(comment, nickname);
            } else {
                comment.setNickname(comment.getNickname().trim());
                setCommentRandomAvatar(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            comment.setNickname(comment.getNickname().trim());
            setCommentRandomAvatar(comment);
        }

        //check website
        if (!isValidUrl(comment.getWebsite())) {
            comment.setWebsite("");
        }
        comment.setAdminComment(false);
        comment.setCreateTime(new Date());
        comment.setPublished(commentDefaultOpen);
        comment.setEmail(comment.getEmail().trim());
        comment.setIp(IpAddressUtils.getIpAddress(request));
    }

    /**
     * 设置QQ头像，复用已上传过的QQ头像，不再重复上传
     *
     * @param comment 当前收到的评论
     * @param qq      QQ号
     * @throws Exception 上传QQ头像时可能抛出的异常
     */
    private void setCommentQQAvatar(Comment comment, String qq) throws Exception {
        String uploadAvatarUrl = qqAvatarCache.get(qq);
        if (StringUtils.isBlank(uploadAvatarUrl)) {
            uploadAvatarUrl = QQInfoUtils.getQQAvatarUrl(qq);
            qqAvatarCache.set(qq, uploadAvatarUrl);
        }
        comment.setAvatar(uploadAvatarUrl);
    }

    /**
     * URL合法性校验
     *
     * @param url url
     * @return 是否合法
     */
    private static boolean isValidUrl(String url) {
        return url.matches("^https?://([^!@#$%^&*?.\\s-]([^!@#$%^&*?.\\s]{0,63}[^!@#$%^&*?.\\s])?\\.)+[a-z]{2,6}/?");
    }
}
