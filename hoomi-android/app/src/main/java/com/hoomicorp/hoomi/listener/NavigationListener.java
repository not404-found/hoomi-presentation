package com.hoomicorp.hoomi.listener;

import com.hoomicorp.hoomi.model.dto.PostDto;

public interface NavigationListener {
    void navigateToGoLiveFragment(PostDto item);
}
