package lkeleti.redditclone.services;

import lkeleti.redditclone.dtos.CreateSubRedditCommand;
import lkeleti.redditclone.dtos.SubRedditDto;
import lkeleti.redditclone.exceptions.SubRedditNotFoundException;
import lkeleti.redditclone.models.SubReddit;
import lkeleti.redditclone.repositories.SubRedditRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SubRedditService {

    private final SubRedditRepository subRedditRepository;
    private ModelMapper modelMapper;

    public SubRedditDto createSubReddit(CreateSubRedditCommand createSubRedditCommand) {
        SubReddit subReddit = new SubReddit(
                createSubRedditCommand.getName(),
                createSubRedditCommand.getDescription()
        );
        return modelMapper.map(subRedditRepository.save(subReddit), SubRedditDto.class);
    }

    public List<SubRedditDto> getAllSubReddits() {
        return mapSubredditsToDtos(subRedditRepository.findAll());
    }

    private List<SubRedditDto> mapSubredditsToDtos(List<SubReddit> all) {
        List<SubRedditDto> srDto= new ArrayList<>();
        for (SubReddit sr : all) {
            srDto.add(
                    new SubRedditDto(
                            sr.getId(),
                            sr.getName(),
                            sr.getDescription(),
                            sr.getPosts().size()
                    )
            );
        }
        return srDto;
    }

    public SubRedditDto findSubRedditById(long id) {
        SubReddit sr = subRedditRepository.findById(id).orElseThrow(
                ()-> new SubRedditNotFoundException(id)
        );
        return new SubRedditDto(
                sr.getId(),
                sr.getName(),
                sr.getDescription(),
                sr.getPosts().size()
        );
    }
}
