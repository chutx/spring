package my.messenger.domain.people;

import java.util.HashSet;
import java.util.Set;

public class User extends Person {

	private String userId;
	private Set<String> roles;
	
	public User(String email, String userId, String firstName, String lastName) {
		super(email, firstName, lastName);
		this.userId = userId;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		if(roles != null){
			this.roles.addAll(roles);
		}else{
			this.roles = roles;
		}
	}
	
	public void addRole(String role){
		if(roles == null){
			roles = new HashSet<>();
		}
		roles.add(role);
	}

	public String getUserId() {
		return userId;
	}
	

}
