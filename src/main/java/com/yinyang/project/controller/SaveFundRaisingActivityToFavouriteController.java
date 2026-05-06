package com.yinyang.project.controller;

import com.yinyang.project.entity.FavouriteFRA;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SaveFundRaisingActivityToFavouriteController {

    private FavouriteFRA favouriteFRA;

    public boolean saveFundRaisingActivityToFavourite(@NotNull Integer fundRaisingActivityId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        if (currentUserRole == UserProfile.Name.DONEE) {
            favouriteFRA = new FavouriteFRA();
            return favouriteFRA.saveFundRaisingActivityToFavourite(currentUserId, fundRaisingActivityId);
        }
        return false;
    }
}
