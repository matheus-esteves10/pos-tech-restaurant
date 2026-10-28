package br.com.fiap.restaurant.presentation.presenter;

import br.com.fiap.restaurant.domain.model.PageResult;
import br.com.fiap.restaurant.domain.model.User;
import br.com.fiap.restaurant.presentation.dto.response.PageResponse;
import br.com.fiap.restaurant.presentation.dto.response.UserResponse;

import java.util.List;

public final class UserPresenter {

    private UserPresenter() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getPhone(),
                user.isEnabled(),
                AddressPresenter.toResponse(user.getAddress())
        );
    }

    public static List<UserResponse> toResponse(List<User> users) {
        return users.stream().map(UserPresenter::toResponse).toList();
    }

    public static PageResponse<UserResponse> toResponse(PageResult<User> page) {
        PageResult<UserResponse> mapped = page.map(UserPresenter::toResponse);
        return new PageResponse<>(mapped.content(), mapped.page(), mapped.size(), mapped.totalElements(),
                mapped.totalPages());
    }
}
