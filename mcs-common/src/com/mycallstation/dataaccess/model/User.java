/**
 * 
 */
package com.mycallstation.dataaccess.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

import com.mycallstation.base.model.AbstractTrackableEntity;
import com.mycallstation.base.model.IdBasedEntity;
import com.mycallstation.constant.AccountStatus;

/**
 * @author Wei Gao
 * 
 */
@Entity
@Table(name = "tbl_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "email", "deletedate" }),
        @UniqueConstraint(columnNames = { "username", "deletedate" }) })
@SQLDelete(sql = "UPDATE tbl_user SET deletedate = CURRENT_TIMESTAMP WHERE id = ?")
public class User extends AbstractTrackableEntity implements
        IdBasedEntity<Long>, Serializable {
    private static final long serialVersionUID = 4305835276667230335L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Basic
    @Column(name = "middle_name", length = 64)
    private String middleName;

    @Basic
    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @Basic
    @Column(name = "display_name", length = 64)
    private String displayName;

    @Basic
    @Column(name = "birthday")
    private Date birthDay;

    @Basic
    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Basic
    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @Basic
    @Column(name = "password", columnDefinition = "char", length = 32)
    private String password;

    @Basic
    @Column(name = "locale", length = 16)
    private Locale locale;

    @Basic
    @Column(name = "time_zone", length = 64)
    private TimeZone timeZone;

    @Enumerated
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(name = "tbl_userrole", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @Filter(name = "defaultFilter", condition = "deletedate = 0")
    private Set<Role> roles;

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mycallstation.base.model.IdBasedEntity#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param middleName
     *            the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return the displayName
     */
    public String getUserDisplayName() {
        if (displayName == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(firstName);
            if (middleName != null) {
                sb.append(" ").append(middleName);
            }
            sb.append(" ").append(lastName);
            return sb.toString();
        } else {
            return displayName;
        }
    }

    /**
     * @param birthDay
     *            the birthDay to set
     */
    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    /**
     * @return the birthDay
     */
    public Date getBirthDay() {
        return birthDay;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param locale
     *            the locale to set
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * @param timeZone
     *            the timeZone to set
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * @return the timeZone
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * @return the status
     */
    public AccountStatus getStatus() {
        return status;
    }

    /**
     * @param roles
     *            the roles to set
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * @return the roles
     */
    public Set<Role> getRoles() {
        return roles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 11;
        int result = 15;
        result = prime * result
                + ((username == null) ? 0 : username.toUpperCase().hashCode());
        result = prime * result
                + ((deleteDate == null) ? 0 : deleteDate.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof User)) {
            return false;
        }
        final User obj = (User) other;
        if (username == null) {
            if (obj.username != null) {
                return false;
            }
        } else if (!username.equalsIgnoreCase(obj.username)) {
            return false;
        }
        if (deleteDate == null) {
            if (obj.deleteDate != null) {
                return false;
            }
        } else if (!deleteDate.equals(obj.deleteDate)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User[");
        if (id != null) {
            sb.append("id=").append(id).append(",");
        }
        sb.append("username=").append(username).append(",displayname=")
                .append(getUserDisplayName()).append(",email=").append(email)
                .append("]");
        return sb.toString();
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }
}
