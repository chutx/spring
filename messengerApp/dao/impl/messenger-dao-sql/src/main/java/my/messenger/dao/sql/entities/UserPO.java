package my.messenger.dao.sql.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="user")
public class UserPO {

	@Id
	private String email;
	
	@Column
	private String firstName;
	
	@Column
	private String lastName;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;
	
	//roles
}
