package lkeleti.jpaauthuserh2.services;

import lkeleti.jpaauthuserh2.exceptions.PostNotFoundException;
import lkeleti.jpaauthuserh2.models.Post;
import lkeleti.jpaauthuserh2.models.PostDto;
import lkeleti.jpaauthuserh2.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;


@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public List<PostDto> findAll() {
        Type targetListType = new TypeToken<List<PostDto>>(){}.getType();
        return modelMapper.map(postRepository.findAll(),targetListType);
    }

    public PostDto findById(long id) {
        Post post = postRepository.findById(id).orElseThrow(
                ()-> new PostNotFoundException(id)
        );
        return modelMapper.map(post, PostDto.class);
    }
}
