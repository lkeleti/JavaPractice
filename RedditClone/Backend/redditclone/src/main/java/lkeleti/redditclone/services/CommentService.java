package lkeleti.redditclone.services;

import lkeleti.redditclone.dtos.CommentDto;
import lkeleti.redditclone.dtos.CreateCommentCommand;
import lkeleti.redditclone.exceptions.PostNotFoundException;
import lkeleti.redditclone.models.Comment;
import lkeleti.redditclone.models.NotificationEmail;
import lkeleti.redditclone.models.Post;
import lkeleti.redditclone.models.User;
import lkeleti.redditclone.repositories.CommentRepository;
import lkeleti.redditclone.repositories.PostRepository;
import lkeleti.redditclone.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {

    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public CommentDto createComment(CreateCommentCommand createCommentCommand) {
        Comment comment = new Comment(
                createCommentCommand.getText(),
                LocalDateTime.now()
        );

        User user = authService.getCurrentUser();
        comment.setUser(user);
        Long postId = createCommentCommand.getPostId();
        Post post = postRepository.findById(postId).orElseThrow(
                ()->new PostNotFoundException(postId)
        );
        comment.setPost(post);
        String message = mailContentBuilder.build(post.getUser().getUserName() + " posted a comment on your post.");
        sendCommentNotification(message,post.getUser());
        return modelMapper.map(commentRepository.save(comment), CommentDto.class);
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUserName() + " commented on your post", user.getEmail(), message));
    }

    public List<CommentDto> getAllCommentsForPost(long postId) {
        Type targetListType = new TypeToken<List<CommentDto>>(){}.getType();
        Post post = postRepository.findById(postId).orElseThrow(
                ()->new PostNotFoundException(postId)
        );
        return modelMapper.map(commentRepository.findAllByPost(post), targetListType);
    }

    public List<CommentDto> getAllCommentsByUsername(String username) {
        Type targetListType = new TypeToken<List<CommentDto>>(){}.getType();
        User user = userRepository.findByUserName(username).orElseThrow(
                ()->new UsernameNotFoundException(username)
        );
        return modelMapper.map(commentRepository.findAllByUser(user), targetListType);
    }
}
