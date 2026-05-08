package com.yinyang.project.controller;

import com.yinyang.project.entity.FavouriteFRA;
import com.yinyang.project.entity.FundRaisingActivity;
import com.yinyang.project.entity.UserProfile;
import com.yinyang.project.utils.ThreadLocalUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SaveFundRaisingActivityToFavouriteController {

    private FavouriteFRA favouriteFRA;

    private FundRaisingActivity fundRaisingActivity;

    public boolean saveFundRaisingActivityToFavourite(@NotNull Integer fundRaisingActivityId) {
        Map<String, Object> claims = ThreadLocalUtil.get();
        UserProfile.Name currentUserRole = UserProfile.Name.valueOf((String) claims.get("role"));
        Integer currentUserId = (Integer) claims.get("id");
        if (currentUserRole == UserProfile.Name.DONEE) {
            favouriteFRA = new FavouriteFRA();
            fundRaisingActivity = new FundRaisingActivity();
            boolean isSaved = favouriteFRA.saveFundRaisingActivityToFavourite(currentUserId, fundRaisingActivityId);
            if (isSaved) {
                fundRaisingActivity.saveFundRaisingActivityToFavourite(fundRaisingActivityId);
                return true;
            }
        }
        return false;
    }
}
