package com.ll.goohaeyou.category.domain.policy;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.category.domain.entity.Category;
import com.ll.goohaeyou.category.exception.CategoryException;
import com.ll.goohaeyou.member.member.domain.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CategoryPolicy {

    public void validateCategoryCreation(Role role, Category parent, int level) {
        verifyIsAdmin(role);
        validateCategoryHierarchy(parent, level);
    }

    // 관리자 권한 검증
     void verifyIsAdmin(Role role) {
        if (!Role.ADMIN.equals(role)) {
            throw new AuthException.NotAuthorizedException();
        }
    }

    // 카테고리 계층의 유효성 검증
    private void validateCategoryHierarchy(Category parent, int level) {
        if (Objects.isNull(parent) && level > 1) {
            throw new CategoryException.InvalidCategoryFormatException();
        }
    }
}
