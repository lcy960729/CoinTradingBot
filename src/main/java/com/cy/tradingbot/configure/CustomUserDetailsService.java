package com.cy.tradingbot.configure;

import com.cy.tradingbot.domain.entity.User;
import com.cy.tradingbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(RuntimeException::new);

        CustomUserDetail customUserDetail = new CustomUserDetail();
        customUserDetail.setUsername(user.getUserName());
        customUserDetail.setPassword(user.getPassword());
        customUserDetail.setAuthorities(new ArrayList<GrantedAuthority>(){{add(new SimpleGrantedAuthority("ROLE_USER"));}});
        customUserDetail.setEnabled(true);
        customUserDetail.setAccountNonExpired(true);
        customUserDetail.setAccountNonLocked(true);
        customUserDetail.setCredentialsNonExpired(true);

        customUserDetail.setUserId(user.getId());

        return customUserDetail;
    }
}
