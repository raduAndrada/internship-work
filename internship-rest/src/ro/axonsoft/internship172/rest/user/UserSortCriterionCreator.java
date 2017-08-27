package ro.axonsoft.internship172.rest.user;

import ro.axonsoft.internship172.model.base.SortDirection;
import ro.axonsoft.internship172.model.user.ImtUserSortCriterion;
import ro.axonsoft.internship172.model.user.UserSortCriterion;
import ro.axonsoft.internship172.model.user.UserSortCriterionType;
import ro.axonsoft.internship172.rest.util.RestUtil.SortCriterionCreator;

public enum UserSortCriterionCreator implements SortCriterionCreator<UserSortCriterionType, UserSortCriterion> {
	USER_SORT_CRITERION_CREATOR;

	@Override
	public UserSortCriterion create(UserSortCriterionType type, SortDirection dir) {
		return ImtUserSortCriterion.of(type, dir);
	}

}
