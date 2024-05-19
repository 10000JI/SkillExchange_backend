package place.skillexchange.backend.talent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import place.skillexchange.backend.talent.dto.PlaceDto;
import place.skillexchange.backend.talent.repository.PlaceRepository;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceDto.PlaceReadResponse list() {
        return new PlaceDto.PlaceReadResponse(placeRepository.findAll());
    }
}
