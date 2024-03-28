CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`%` 
    SQL SECURITY DEFINER
VIEW `wp_user_roles_view` AS
    SELECT 
        `wp_options`.`option_value` AS `option_value`
    FROM
        `wp_options`
    WHERE
        `wp_options`.`option_name` = 'wp_user_roles'