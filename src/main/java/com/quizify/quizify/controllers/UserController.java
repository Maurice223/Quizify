package com.quizify.quizify.controllers;

import com.quizify.quizify.entities.User;
import com.quizify.quizify.repositories.UserRepository;
import com.quizify.quizify.services.EmailService;

import java.util.List;
import java.util.UUID;

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

    @Autowired
    private EmailService emailService;

    // INSCRIPTION : Email et Username uniques
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // 1. Vérifications (ton code actuel)
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ce nom d'utilisateur est déjà pris.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cet email est déjà utilisé.");
        }

        // 2. Sauvegarde de l'utilisateur
        User savedUser = userRepository.save(user);

        // 3. Envoi de l'email (dans un bloc try/catch pour ne pas bloquer l'inscription
        // si le mail échoue)
        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getUsername());
        } catch (Exception e) {
            // On affiche l'erreur dans la console Debian pour le debug
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }

        return ResponseEntity.ok(savedUser);
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

    @PostMapping("/forgot-username")
    public ResponseEntity<?> forgotUsername(@RequestParam String email) {
        // a. Chercher l'utilisateur par email
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun compte n'est associé à cet email.");
        }

        // b. Envoyer le mail avec le username trouvé en base
        emailService.sendForgotUsernameEmail(user.getEmail(), user.getUsername());

        return ResponseEntity.ok("Un email contenant votre identifiant vous a été envoyé.");
    }
}