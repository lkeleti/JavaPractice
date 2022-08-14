package lkeleti.redditclone.services;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import lkeleti.redditclone.dtos.CreatePostCommand;
import lkeleti.redditclone.dtos.PostDto;
import lkeleti.redditclone.exceptions.PostNotFoundException;
import lkeleti.redditclone.exceptions.SubRedditNameNotFoundException;
import lkeleti.redditclone.exceptions.SubRedditNotFoundException;
import lkeleti.redditclone.models.*;
import lkeleti.redditclone.repositories.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final SubRedditRepository subRedditRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final VoteRepository voteRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    public PostDto createPost(CreatePostCommand createPostCommand) {
        Post post = new Post(
                createPostCommand.getPostName(),
                createPostCommand.getUrl(),
                createPostCommand.getDescription(),
                0,
                LocalDateTime.now()
        );
        String subRedditName = createPostCommand.getSubRedditName();
        SubReddit sr = subRedditRepository.findByName(subRedditName).orElseThrow(
                ()-> new SubRedditNameNotFoundException(subRedditName)
        );
        post.setSubreddit(sr);
        post.setUser(authService.getCurrentUser());
        return mapToDto(postRepository.save(post));
    }

    public PostDto getPostById(long id) {
        return mapToDto(
                postRepository.findById(id).orElseThrow(
                        ()-> new PostNotFoundException(id)
                )
        );
    }

    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<PostDto> getPostsBySubRedditId(long id) {
        List<Post> posts = subRedditRepository.findById(id).orElseThrow(
                ()-> new SubRedditNotFoundException(id)
        ).getPosts();
        return posts.stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<PostDto> getPostsByUser(String username) {
        User user = userRepository.findByUserName(username).orElseThrow(
                ()-> new UsernameNotFoundException(username)
        );
        return postRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .toList();
    }

    private PostDto mapToDto(Post post) {
        PostDto postDto = modelMapper.map(post, PostDto.class);

        postDto.setSubRedditName(post.getSubreddit().getName());
        postDto.setUserName(post.getUser().getUserName());
        List<Vote> votes = voteRepository.findAllByPost(post);
        int voteCount = 0;
        for (Vote vote : votes) {
            voteCount += vote.getVoteType().getDirection();
        }
        postDto.setVoteCount(voteCount);

        int commentCount = commentRepository.findAllByPost(post).size();
        postDto.setCommentCount(commentCount);
        postDto.setDuration(TimeAgo.using(post.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        return postDto;
    }
}
