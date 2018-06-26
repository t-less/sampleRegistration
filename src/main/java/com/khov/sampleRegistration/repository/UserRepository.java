package com.khov.sampleRegistration.repository;

import com.khov.sampleRegistration.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author t-less
 */
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("select u from User u where u.email = :email")
    User findByEmail(@Param("email") String email);

    @Query("select u from User u where u.id = :id")
    User findUserById(@Param("id") Integer id);
}
