package com.carnasa.cr.projectkingdomwebpage.repositories.user;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserExtra;
import com.carnasa.cr.projectkingdomwebpage.entities.user.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    void deleteByUserExtra(UserExtra userExtra);
}
