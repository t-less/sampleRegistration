package com.khov.sampleRegistration.repository;

import com.khov.sampleRegistration.model.Privilege;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author t-less
 */
@Repository
public interface PrivilegeRepository extends CrudRepository <Privilege, Integer> {
    
    Privilege findByName(String name);
    
}
