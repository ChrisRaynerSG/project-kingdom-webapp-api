package com.carnasa.cr.projectkingdomwebpage.repositories.user;

import com.carnasa.cr.projectkingdomwebpage.entities.user.UserExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserExtraRepository extends JpaRepository<UserExtra, Long> {


}
