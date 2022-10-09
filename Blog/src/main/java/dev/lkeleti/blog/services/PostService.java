package dev.lkeleti.blog.services;

import dev.lkeleti.blog.models.PostDto;
import dev.lkeleti.blog.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    public final PostRepository postRepository;
    public final ModelMapper modelmapper;


    public List<PostDto> listAllPosts() {
        Type targetListType = new TypeToken<List<PostDto>>(){}.getType();
        return modelmapper.map(postRepository.findAll(), targetListType);
    }

    public PostDto listPostById(long id) {
        return modelmapper.map(
                postRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("Invalid id!")
                )
                , PostDto.class
        );
    }
}
