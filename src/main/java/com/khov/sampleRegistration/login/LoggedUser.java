package com.khov.sampleRegistration.login;

import com.khov.sampleRegistration.model.Role;


/**
 *
 * @author t-less
 */
public class LoggedUser {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private Role mainRole;

    public LoggedUser() {

    }

    public LoggedUser(Integer id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the mainRole
     */
    public Role getMainRole() {
        return mainRole;
    }

    /**
     * @param mainRole the mainRole to set
     */
    public void setMainRole(Role mainRole) {
        this.mainRole = mainRole;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
