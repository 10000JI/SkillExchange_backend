package place.skillexchange.backend.talent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import place.skillexchange.backend.talent.dto.PlaceDto;
import place.skillexchange.backend.talent.service.PlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/place/")
public class PlaceController {

    private final PlaceService placeService;

    /**
     * 공지사항 조회
     */
    @GetMapping("/list")
    public PlaceDto.PlaceReadResponse list() {
        return placeService.list();
    }
}
