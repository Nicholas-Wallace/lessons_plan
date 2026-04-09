package com.nicholaswallace.lessons_plan.service;

import com.nicholaswallace.lessons_plan.model.AppUser;

public interface MagicLinkEmailSender {
    void sendSignInLink(AppUser user, String magicLink);
}
