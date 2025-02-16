package com.carnasa.cr.projectkingdomwebpage.controllers.devlog;

import com.carnasa.cr.projectkingdomwebpage.entities.devlog.DevLogPostCategory;
import com.carnasa.cr.projectkingdomwebpage.exceptions.status.NotFoundException;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.create.DevLogPostCategoryPostDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.read.DevLogPostCategoryDto;
import com.carnasa.cr.projectkingdomwebpage.models.devlog.update.DevLogPostCategoryPatchDto;
import com.carnasa.cr.projectkingdomwebpage.services.interfaces.DevLogPostService;
import com.carnasa.cr.projectkingdomwebpage.utils.DevLogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.carnasa.cr.projectkingdomwebpage.controllers.devlog.DevLogPostController.*;

@RestController
public class DevLogCategoryController {

    private final DevLogPostService devLogPostService;

    @Autowired
    public DevLogCategoryController(DevLogPostService devLogPostService) {
        this.devLogPostService = devLogPostService;
    }

    //Create
    @PostMapping(DEV_LOG_POST_CATEGORY_URL)
    public ResponseEntity<DevLogPostCategoryDto> postDevLogPostCategory(@RequestBody DevLogPostCategoryPostDto newCategory) {
        DevLogPostCategory savedCategory = devLogPostService.createDevLogPostCategory(newCategory);
        return new ResponseEntity<>(DevLogUtils.toDto(savedCategory), HttpStatus.CREATED);
    }

    //Read
    /**
     * @return all categories for the Dev Log entity/model
     */
    @GetMapping(DEV_LOG_POST_CATEGORY_URL)
    public ResponseEntity<List<DevLogPostCategoryDto>> getDevLogCategories() {
        return new ResponseEntity<>(devLogPostService.getDevLogPostCategories(), HttpStatus.OK);
    }

    /**
     *
     * @param id id of the category
     * @return single instance of a Dev Log category
     */
    @GetMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> getDevLogPostCategory(@PathVariable Long id) {
        if(devLogPostService.getCategoryDto(id).isEmpty()) {
            throw new NotFoundException("No posts found with id " + id);
        }
        return new ResponseEntity<>(devLogPostService.getCategoryDto(id).get(),HttpStatus.OK);
    }

    //Update

    @PatchMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> patchDevLogPostCategory(@RequestBody DevLogPostCategoryPatchDto update, @PathVariable Long id) {
        DevLogPostCategoryDto updatedPost = devLogPostService.updateDevLogPostCategory(update, id);
        if(updatedPost==null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    //Delete

    @DeleteMapping(DEV_LOG_POST_CATEGORY_URL + "/{id}")
    public ResponseEntity<DevLogPostCategoryDto> deleteDevLogPostCategory(@PathVariable Long id) {
        devLogPostService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
