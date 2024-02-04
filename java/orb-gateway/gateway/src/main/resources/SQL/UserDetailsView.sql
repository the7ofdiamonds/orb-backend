CREATE VIEW user_details_view AS
SELECT u.ID AS id, u.user_email AS email, u.display_name AS username, u.user_pass AS passowrd,
	MAX(CASE WHEN m.meta_key = 'phone_number' THEN m.meta_value END) AS phone,
    MAX(CASE WHEN m.meta_key = 'first_name' THEN m.meta_value END) AS firstname,
	MAX(CASE WHEN m.meta_key = 'last_name' THEN m.meta_value END) AS lastname,
	MAX(CASE WHEN m.meta_key = 'wp_capabilities' THEN m.meta_value END) AS roles,
	MAX(CASE WHEN m.meta_key = 'description' THEN m.meta_value END) AS description,
	MAX(CASE WHEN m.meta_key = 'providerGivenID' THEN m.meta_value END) AS providerGivenID,
	MAX(CASE WHEN m.meta_key = 'isAuthenticated' THEN m.meta_value END) AS isAuthenticated,
	MAX(CASE WHEN m.meta_key = 'isAccountNonExpired' THEN m.meta_value END) AS isAccountNonExpired,
	MAX(CASE WHEN m.meta_key = 'isAccountNonLocked' THEN m.meta_value END) AS isAccountNonLocked,
	MAX(CASE WHEN m.meta_key = 'isCredentialsNonExpired' THEN m.meta_value END) AS isCredentialsNonExpired,
	MAX(CASE WHEN m.meta_key = 'isEnabled' THEN m.meta_value END) AS isEnabled
FROM wp_users u
LEFT JOIN wp_usermeta m ON u.ID = m.user_id
WHERE u.user_login = 'jamel.c.lyons@gmail.com'
GROUP BY u.ID, u.user_login, u.user_email;
