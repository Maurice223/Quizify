package com.quizify.quizify.controllers;

import com.quizify.quizify.entities.User;
import com.quizify.quizify.repositories.UserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // INSCRIPTION : Email et Username uniques
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce nom d'utilisateur est déjà pris.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cet email est déjà utilisé.");
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    // LOGIN : On vérifie juste si le username existe
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return userRepository.findByUsername(user.getUsername())
                .map(u -> ResponseEntity.ok((Object) u)) // On cast en Object pour la flexibilité
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nom d'utilisateur inconnu."));
    }

    // 3. VOIR TOUS LES UTILISATEURS
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 4. VOIR UN UTILISATEUR PAR ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok().body(user))
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. SUPPRIMER UN UTILISATEUR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}