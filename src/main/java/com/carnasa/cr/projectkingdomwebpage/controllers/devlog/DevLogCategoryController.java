package com.carnasa.cr.projectkingdomwebpage.controllers.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostCategory;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostCategoryPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostCategoryDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostCategoryPatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.carnasa.cr.projectkingdomwebpage.utils.LoggingUtils.*;
import static com.carnasa.cr.projectkingdomwebpage.utils.UrlUtils.*;

@RestController
public class DevLogCategoryController {

    public static Logger logger = LoggerFactory.getLogger(DevLogCategoryController.class);
    private final DevLogPostService devLogPostService;

    @Autowired
    public DevLogCategoryController(DevLogPostService devLogPostService) {
        logger.trace("Creating DevLogCategoryController");
        this.devLogPostService = devLogPostService;
    }

    //Create
    @PostMapping(DEV_LOG_POST_CATEGORY_URL)
    public ResponseEntity<DevLogPostCategoryDto> postDevLogPostCategory(@RequestBody DevLogPostCategoryPostDto newCategory) {
        logger.trace(POST_ENDPOINT_LOG_HIT, DEV_LOG_POST_CATEGORY_URL);
        DevLogPostCategory savedCategory = devLogPostService.createDevLogPostCategory(newCategory);
        return new ResponseEntity<>(DevLogUtils.toDto(savedCategory), HttpStatus.CREATED);
    }

    //Read
    /**
     * @return all categories for the Dev Log entity/model
     */
    @GetMapping(DEV_LOG_POST_CATEGORY_URL)
    public ResponseEntity<List<DevLogPostCategoryDto>> getDevLogCategories() {
        logger.trace(GET_ENDPOINT_LOG_HIT, DEV_LOG_POST_CATEGORY_URL);
        return new ResponseEntity<>(devLogPostService.getDevLogPostCategories(), HttpStatus.OK);
    }

    /**
     *
     * @param id id of the category
     * @return single instance of a Dev Log category
     */
    @GetMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> getDevLogPostCategory(@PathVariable Long id) {
        logger.trace(GET_ENDPOINT_LOG_HIT, DEV_LOG_POST_CATEGORY_URL + "/" + id);
        if(devLogPostService.getCategoryDto(id).isEmpty()) {

            logger.warn("Category with id: {} not found", id);
            throw new NotFoundException("No posts found with id: " + id);
        }

        logger.trace("Category with id: {} found", id);
        return new ResponseEntity<>(devLogPostService.getCategoryDto(id).get(),HttpStatus.OK);
    }

    //Update

    @PatchMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> patchDevLogPostCategory(@RequestBody DevLogPostCategoryPatchDto update, @PathVariable Long id) {
        logger.trace(PATCH_ENDPOINT_LOG_HIT, DEV_LOG_POST_CATEGORY_URL + "/" + id);
        DevLogPostCategoryDto updatedPost = devLogPostService.updateDevLogPostCategory(update, id);
        if(updatedPost==null){
            logger.warn("No content found in update, category with ID: {} not updated", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        logger.trace("Category with id: {} updated", id);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    //Delete

    @DeleteMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> deleteDevLogPostCategory(@PathVariable Long id) {
        logger.trace(DELETE_ENDPOINT_LOG_HIT, DEV_LOG_POST_CATEGORY_URL + "/" + id);
        devLogPostService.deleteCategory(id);
        logger.trace("Category with id: {} deleted", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
