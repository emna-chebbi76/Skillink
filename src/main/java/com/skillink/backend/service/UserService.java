package com.skillink.backend.service;

import com.skillink.backend.dto.UserDto;
import com.skillink.backend.entity.User;
import com.skillink.backend.exception.ResourceNotFoundException;
import com.skillink.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + id));
    }

    public UserDto update(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + id));

        if (updates.containsKey("nom"))       user.setNom((String) updates.get("nom"));
        if (updates.containsKey("prenom"))    user.setPrenom((String) updates.get("prenom"));
        if (updates.containsKey("telephone")) user.setTelephone((String) updates.get("telephone"));
        if (updates.containsKey("bio"))       user.setBio((String) updates.get("bio"));
        if (updates.containsKey("quartier"))  user.setQuartier((String) updates.get("quartier"));
        if (updates.containsKey("avatar"))    user.setAvatar((String) updates.get("avatar"));
        if (updates.containsKey("password"))  user.setPassword(passwordEncoder.encode((String) updates.get("password")));

        return UserDto.from(userRepository.save(user));
    }

    public UserDto updateRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé : " + id));
        user.setRole(com.skillink.backend.entity.Role.valueOf(role.toUpperCase()));
        return UserDto.from(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("Utilisateur non trouvé : " + id);
        userRepository.deleteById(id);
    }
}
