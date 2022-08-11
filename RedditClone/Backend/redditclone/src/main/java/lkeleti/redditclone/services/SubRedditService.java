package lkeleti.redditclone.services;

import lkeleti.redditclone.dtos.CreateSubRedditCommand;
import lkeleti.redditclone.dtos.SubRedditDto;
import lkeleti.redditclone.models.SubReddit;
import lkeleti.redditclone.repositories.SubRedditRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
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
        Type targetListType = new TypeToken<List<SubRedditDto>>(){}.getType();
        return modelMapper.map(subRedditRepository.findAll(), targetListType);
    }
}
