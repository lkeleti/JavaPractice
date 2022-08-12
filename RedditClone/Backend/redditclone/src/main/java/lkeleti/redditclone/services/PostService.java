package lkeleti.redditclone.services;

import lkeleti.redditclone.dtos.CreatePostCommand;
import lkeleti.redditclone.dtos.PostDto;
import lkeleti.redditclone.exceptions.PostNotFoundException;
import lkeleti.redditclone.exceptions.SubRedditNameNotFoundException;
import lkeleti.redditclone.exceptions.SubRedditNotFoundException;
import lkeleti.redditclone.models.Post;
import lkeleti.redditclone.models.SubReddit;
import lkeleti.redditclone.models.User;
import lkeleti.redditclone.repositories.PostRepository;
import lkeleti.redditclone.repositories.SubRedditRepository;
import lkeleti.redditclone.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final SubRedditRepository subRedditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

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
                .map(PostService::mapToDto)
                .toList();
    }

    public List<PostDto> getPostsBySubRedditId(long id) {
        List<Post> posts = subRedditRepository.findById(id).orElseThrow(
                ()-> new SubRedditNotFoundException(id)
        ).getPosts();
        return posts.stream()
                .map(PostService::mapToDto)
                .toList();
    }

    public List<PostDto> getPostsByUser(String username) {
        User user = userRepository.findByUserName(username).orElseThrow(
                ()-> new UsernameNotFoundException(username)
        );
        return postRepository.findByUser(user).stream()
                .map(PostService::mapToDto)
                .toList();
    }

    private static PostDto mapToDto(Post post) {
        ModelMapper modelMapper = new ModelMapper();
        PostDto postDto = modelMapper.map(post, PostDto.class);
        postDto.setSubRedditName(post.getSubreddit().getName());
        postDto.setUserName(post.getUser().getUserName());
        return postDto;
    }

}
