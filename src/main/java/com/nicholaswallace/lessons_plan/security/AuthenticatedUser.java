package com.nicholaswallace.lessons_plan.security;

import com.nicholaswallace.lessons_plan.model.Plan;
import com.nicholaswallace.lessons_plan.model.Role;

public record AuthenticatedUser(Long userId, String email, Role role, Plan plan) {
}
