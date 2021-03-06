package in.clouthink.synergy.account.service.impl;

import in.clouthink.synergy.account.domain.model.Role;
import in.clouthink.synergy.account.domain.model.Roles;
import in.clouthink.synergy.account.domain.model.User;
import in.clouthink.synergy.account.domain.model.UserRoleRelationship;
import in.clouthink.synergy.account.domain.request.AbstractUserRequest;
import in.clouthink.synergy.account.domain.request.ChangeUserProfileRequest;
import in.clouthink.synergy.account.domain.request.SaveUserRequest;
import in.clouthink.synergy.account.domain.request.UserSearchRequest;
import in.clouthink.synergy.account.exception.UserException;
import in.clouthink.synergy.account.exception.UserNotFoundException;
import in.clouthink.synergy.account.exception.UserPasswordException;
import in.clouthink.synergy.account.repository.UserRepository;
import in.clouthink.synergy.account.repository.UserRoleRelationshipRepository;
import in.clouthink.synergy.account.service.AccountService;
import in.clouthink.synergy.shared.DomainConstants;
import in.clouthink.synergy.shared.util.I18nUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Log logger = LogFactory.getLog(AccountServiceImpl.class);

    @Autowired
    private PasswordEncoderProvider passwordEncoderProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRelationshipRepository userRoleRelationshipRepository;

    @Override
    public User findById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User findByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        username = username.trim().toLowerCase();
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> listUsers(UserSearchRequest userQueryRequest) {
        return userRepository.queryPage(userQueryRequest);
    }

    @Override
    public Page<User> listUsersByRole(Role role, UserSearchRequest userQueryRequest) {
        return userRepository.queryPage(role, userQueryRequest);
    }

    @Override
    public Page<User> listArchivedUsers(UserSearchRequest userQueryRequest) {
        return userRepository.queryArchivedUsers(userQueryRequest);
    }

    @Override
    public User createAccount(SaveUserRequest saveUserRequest, Role... roles) {
        if (StringUtils.isEmpty(saveUserRequest.getUsername())) {
            throw new UserException("用户名不能为空");
        }
        checkSaveUserRequest(saveUserRequest);

        User existedUser = findByUsername(saveUserRequest.getUsername());
        if (existedUser != null) {
            throw new UserException(String.format("重复的用户名'%s'!", saveUserRequest.getUsername()));
        }

        User userByPhone = userRepository.findByTelephone(saveUserRequest.getTelephone());
        if (userByPhone != null) {
            throw new UserException(String.format("重复的联系电话'%s'!", saveUserRequest.getTelephone()));
        }

        User userByEmail = userRepository.findByEmail(saveUserRequest.getEmail());
        if (userByEmail != null) {
            throw new UserException(String.format("重复的电子邮箱'%s'!", saveUserRequest.getEmail()));
        }

        String password = saveUserRequest.getPassword();
        if (StringUtils.isEmpty(password)) {
            logger.warn(String.format("The password not provided for user[username=%s], we will generate a random one ",
                                      saveUserRequest.getUsername()));
            password = UUID.randomUUID().toString().replace("-", "");
        }
        if (password.length() < 8) {
            throw new UserPasswordException("新设置的密码长度不能少于8位");
        }

        String passwordHash = passwordEncoderProvider.getPasswordEncoder().encode(password);

        User user = new User();

        user.setUsername(saveUserRequest.getUsername());
        user.setPinyin(I18nUtils.chineseToPinyin(saveUserRequest.getUsername()));
        user.setAvatarId(saveUserRequest.getAvatarId());

        user.setPassword(passwordHash);
        user.setTelephone(saveUserRequest.getTelephone());
        user.setEmail(saveUserRequest.getEmail());
        user.setGender(saveUserRequest.getGender());
        user.setBirthday(saveUserRequest.getBirthday());
        user.setEnabled(true);

        user.setCreatedAt(new Date());

        final User result = userRepository.save(user);

        Stream.of(roles).forEach(role -> {
            UserRoleRelationship relationship = userRoleRelationshipRepository.findByUserAndRole(result, role);
            if (relationship == null) {
                relationship = new UserRoleRelationship();
                relationship.setUser(result);
                relationship.setRole(role);
                userRoleRelationshipRepository.save(relationship);
            }
        });

        return result;
    }


    @Override
    public User updateAccount(String userId, SaveUserRequest request) {
        if (StringUtils.isEmpty(userId)) {
            throw new UserException("用户id不能为空");
        }
        User existedUser = findById(userId);
        if (existedUser == null) {
            throw new UserNotFoundException(userId);
        }

        checkSaveUserRequest(request);

        User userByPhone = userRepository.findByTelephone(request.getTelephone());
        if (userByPhone != null && !userByPhone.getId().equals(userId)) {
            throw new UserException(String.format("重复的联系电话'%s'!", request.getTelephone()));
        }

        User userByEmail = userRepository.findByEmail(request.getEmail());
        if (userByEmail != null && !userByEmail.getId().equals(userId)) {
            throw new UserException(String.format("重复的电子邮箱'%s'!", request.getEmail()));
        }

        existedUser.setEmail(request.getEmail());
        existedUser.setTelephone(request.getTelephone());
        existedUser.setGender(request.getGender());
        existedUser.setBirthday(request.getBirthday());
        existedUser.setModifiedAt(new Date());

        return userRepository.save(existedUser);
    }

    @Override
    public User changeUserProfile(String userId, ChangeUserProfileRequest request) {
        if (StringUtils.isEmpty(userId)) {
            throw new UserException("用户id不能为空");
        }
        User existedUser = findById(userId);
        if (existedUser == null) {
            throw new UserNotFoundException(userId);
        }

        checkSaveUserRequest(request);

        User userByPhone = userRepository.findByTelephone(request.getTelephone());
        if (userByPhone != null && !userByPhone.getId().equals(userId)) {
            throw new UserException(String.format("重复的联系电话'%s'!", request.getTelephone()));
        }

        User userByEmail = userRepository.findByEmail(request.getEmail());
        if (userByEmail != null && !userByEmail.getId().equals(userId)) {
            throw new UserException(String.format("重复的电子邮箱'%s'!", request.getEmail()));
        }

        existedUser.setEmail(request.getEmail());
        existedUser.setTelephone(request.getTelephone());
        existedUser.setGender(request.getGender());
        existedUser.setBirthday(request.getBirthday());
        existedUser.setModifiedAt(new Date());

        return userRepository.save(existedUser);
    }

    @Override
    public User changeUserAvatar(String userId, String avatarId, String avatarUrl) {
        if (StringUtils.isEmpty(userId)) {
            throw new UserException("用户id不能为空");
        }
        User existedUser = findById(userId);
        if (existedUser == null) {
            throw new UserNotFoundException(userId);
        }

        existedUser.setAvatarId(avatarId);
        existedUser.setAvatarUrl(avatarUrl);
        return userRepository.save(existedUser);
    }

    @Override
    public User enable(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    public User disable(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        user.setEnabled(false);
        return userRepository.save(user);
    }

    @Override
    public User lock(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        user.setLocked(true);
        return userRepository.save(user);
    }

    @Override
    public User unlock(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        user.setLocked(false);
        return userRepository.save(user);
    }

    @Override
    public User changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        if (StringUtils.isEmpty(oldPassword)) {
            throw new UserPasswordException("原密码不能为空");
        }

        if (StringUtils.isEmpty(newPassword)) {
            throw new UserPasswordException("新设置的密码不能为空");
        }

        if (newPassword.length() < 8) {
            throw new UserPasswordException("新设置的密码长度不能少于8位");
        }

        if (!passwordEncoderProvider.getPasswordEncoder().matches(oldPassword, user.getPassword())) {
            throw new UserPasswordException("原密码错误");
        }

        if (newPassword.equals(oldPassword)) {
            throw new UserPasswordException("新设置的密码和旧密码不能相同");
        }

        String passwordHash = passwordEncoderProvider.getPasswordEncoder().encode(newPassword);
        user.setPassword(passwordHash);
        return userRepository.save(user);
    }

    @Override
    public User changePassword(String userId, String newPassword) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        if (StringUtils.isEmpty(newPassword)) {
            throw new UserPasswordException("新设置的密码不能为空");
        }

        if (newPassword.length() < 8) {
            throw new UserPasswordException("新设置的密码长度不能少于8位");
        }

        String passwordHash = passwordEncoderProvider.getPasswordEncoder().encode(newPassword);
        user.setPassword(passwordHash);
        return userRepository.save(user);
    }

    @Override
    public void forgetPassword(String username) {
        if (StringUtils.isEmpty(username)) {
            return;
        }
        username = username.trim().toLowerCase();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        }
        //TODO send passcode to reset password
    }

    @Override
    public void deleteUser(String userId, User byWho) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return;
        }

        //		if (!"administrator".equals(byWho.getUsername())) {
        //			throw new UserException("只有超级管理员能彻底删除用户!");
        //		}

        throw new UnsupportedOperationException("目前不支持彻底删除用户!");
    }

    @Override
    public void archiveAccount(String userId, User byWho) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return;
        }

        if ("administrator".equals(user.getUsername())) {
            throw new UserException("不能删除超级管理员");
        }

        if (byWho == null ||
                (!byWho.getAuthorities().contains(Roles.ROLE_MGR) &&
                        !byWho.getAuthorities().contains(Roles.ROLE_ADMIN))) {
            throw new UserException("只有管理员和超级管理员能删除已经创建的用户!");
        }

        user.setUsername(user.getUsername() + "(已删除)");
        user.setTelephone(user.getTelephone() + "(已删除)");
        user.setEmail(user.getEmail() + "(已删除)");
        user.setEnabled(false);
        user.setLocked(true);
        user.setExpired(true);
        user.setPassword(UUID.randomUUID().toString());
        user.setArchived(true);
        user.setArchivedAt(new Date());

        userRepository.save(user);
    }

    private void checkSaveUserRequest(AbstractUserRequest request) {
        if (StringUtils.isEmpty(request.getTelephone())) {
            throw new UserException("联系电话不能为空");
        }
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new UserException("邮箱不能为空");
        }
        if (!DomainConstants.VALID_CELLPHONE_REGEX.matcher(request.getTelephone()).matches()) {
            throw new UserException("联系电话格式错误,请输入手机号码");
        }
        if (!StringUtils.isEmpty(request.getEmail()) &&
                !DomainConstants.VALID_EMAIL_REGEX.matcher(request.getEmail()).matches()) {
            throw new UserException("电子邮箱格式错误");
        }
    }

}
