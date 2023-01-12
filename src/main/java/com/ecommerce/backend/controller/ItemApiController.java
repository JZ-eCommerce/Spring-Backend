package com.ecommerce.backend.controller;

import com.ecommerce.backend.JUINResponse;
import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.request.ItemRequest;
import com.ecommerce.backend.domain.response.ItemResponse;
import com.ecommerce.backend.service.command.ItemCommandService;
import com.ecommerce.backend.service.query.ItemQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;

@Api(tags = {"05. Item"})
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/items")
public class ItemApiController {
    private final ItemQueryService itemQueryService;

    private final ItemCommandService itemCommandService;

    @ApiOperation(value = "판매자 상품 등록", notes = "관리자가 상품을 등록한다.")
    @PostMapping(value = "/seller/register", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public JUINResponse<Long> register(@RequestPart ItemRequest.Create request,
                                       @RequestPart(value = "thumbnail") MultipartFile thumbnailImage,
                                       @RequestPart(value = "fileList", required = false) List<MultipartFile> itemImageFileList) {

        log.info("[P9][CON][ITEM][REGI]: GET /api/items/seller/register request({}), thumbnail({}), fileList({})"
                , request
                , thumbnailImage
                , itemImageFileList);

        try {

            var response = itemCommandService.add(request, thumbnailImage, itemImageFileList);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (InvalidParameterException e) {
            log.warn("상품 썸네일은 필수입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        } catch (Exception e) {
            log.warn("파일 등록에 실패하였습니다.");
            return new JUINResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @ApiOperation(value = "판매자 상품 읽기", notes = "관리자 페이지에서 상품을 읽는다.")
    @GetMapping("/seller/{itemId}")
    public JUINResponse<ItemResponse.Read> adminRead(@PathVariable Long itemId) {
        log.info("[P9][CON][ITEM][AMRD]: GET /api/items/seller/{itemId} itemId({})", itemId);

        try {
            final Item item = itemQueryService.readById(itemId);

            var response = ItemResponse.Read.from(item);
            return new JUINResponse<>(HttpStatus.OK, "상품 읽기 성공", response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "판매자 상품 삭제", notes = "관리자 페이지에서 상품을 삭제.")
    @DeleteMapping("/seller/{itemId}")
    public JUINResponse<Long> adminRemove(@PathVariable Long itemId) {
        log.info("[P9][CON][ITEM][AMRM]: DELETE /api/items/seller/{itemId} itemId({})", itemId);

        try {

            var response = itemCommandService.remove(itemId);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 읽기", notes = "상품을 읽는다.")
    @GetMapping("/{itemId}")
    public JUINResponse<ItemResponse.Read> retrieveOne(@PathVariable Long itemId) {
        log.info("[P9][CON][ITEM][ONE_]: GET /api/items/{itemId} itemId({})", itemId);

        try {
            final Item item = itemQueryService.readById(itemId);

            var response = ItemResponse.Read.from(item);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "상품 목록 읽기", notes = "전체 또는 카테고리 별 상품 조회")
    @GetMapping
    public JUINResponse<List<ItemResponse.Read>> retrieveAll(@PageableDefault(size = 10) Pageable pageable,
                                                             @RequestParam(required = false) Long categoryId) {
        log.info("[P9][CON][ITEM][ALL_]: GET /api/items pageable({}), categoryId({})", pageable, categoryId);

        try {
            var response = itemQueryService.display(pageable, categoryId);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "전체 상품의 개수", notes = "전체 상품의 개수를 반환한다.")
    @GetMapping("/count")
    public long retrieveCount() {
        log.info("[P9][CON][ITEM][CNT_]: GET /api/items/count");
        return itemQueryService.total();
    }

    @ApiOperation(value = "상품 검색하기", notes = "전체 또는 특정 카테고리에서 상품을 상품이름으로 검색해서 찾는다")
    @GetMapping("/search")
    public JUINResponse<List<ItemResponse.Read>> search(@PageableDefault(size = 10) Pageable pageable,
                                                        @RequestParam("name") String searchTitle,
                                                        @RequestParam(required = false) Long categoryId) {
        log.info("[P9][CON][ITEM][SRCH]: GET /api/items/search pageable({}), searchTitle({}), categoryId({})",
                pageable, searchTitle, categoryId);

        try {
            var response = itemQueryService.search(pageable, searchTitle, categoryId);
            return new JUINResponse<>(HttpStatus.OK, response);
        } catch (EntityNotFoundException e) {
            log.warn("존재하지 않는 Entity입니다. message: ({})", e.getMessage(), e);
            return new JUINResponse<>(HttpStatus.BAD_REQUEST, null);
        }
    }

    @ApiOperation(value = "검색한 상품의 개수", notes = "검색한 상품의 개수를 반환한다.")
    @GetMapping("/search/count")
    public Long retrieveSearchCount(@RequestParam("name") String searchTitle) {
        log.info("[P9][CON][ITEM][SHCT]: GET /api/items/search/count searchTitle({})", searchTitle);

        return itemQueryService.totalByNameContaining(searchTitle);
    }
}

