package ma.fstg.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Vérifie le tableau de l'étape 8 et les règles des demandes,
 * avec les vrais filtres de sécurité et une base H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FluxAuthentificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    private FormLoginRequestBuilder connexion(String nomCompte, String codeSecret) {
        return formLogin("/authenticate")
                .userParameter("nomCompte").user(nomCompte)
                .passwordParam("codeSecret").password(codeSecret);
    }

    @Test
    void sansConnexionLesPagesRedirigentVersLogin() throws Exception {
        for (String page : new String[] {"/", "/home", "/user/dashboard", "/admin/dashboard"}) {
            mockMvc.perform(get(page)).andExpect(redirectedUrlPattern("**/login"));
        }
    }

    @Test
    void laPageDeConnexionEtLeStyleSontPublics() throws Exception {
        mockMvc.perform(get("/login")).andExpect(status().isOk());
        mockMvc.perform(get("/css/style.css")).andExpect(status().isOk());
    }

    @Test
    void uneConnexionReussieMeneToujoursAHome() throws Exception {
        mockMvc.perform(connexion("user", "1111"))
                .andExpect(authenticated().withUsername("user").withRoles("USER"))
                .andExpect(redirectedUrl("/home"));
        mockMvc.perform(connexion("admin", "1234"))
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    void unMauvaisMotDePasseRevientSurLoginAvecErreur() throws Exception {
        mockMvc.perform(connexion("user", "mauvais"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void laDeconnexionRevientSurLoginAvecConfirmation() throws Exception {
        mockMvc.perform(logout("/logout")).andExpect(redirectedUrl("/login?logout=true"));
    }

    @Test
    void leMessageDeLoginSuitLeParametreDeLUrl() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(content().string(not(containsString("Identifiants incorrects"))));
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(content().string(containsString("Identifiants incorrects")));
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(content().string(containsString("déconnecté avec succès")));
    }

    @Test
    void lesRolesLimitentLAccesAuxEspaces() throws Exception {
        mockMvc.perform(get("/user/dashboard").with(user("user").roles("USER"))).andExpect(status().isOk());
        mockMvc.perform(get("/admin/dashboard").with(user("user").roles("USER"))).andExpect(status().isForbidden());
        mockMvc.perform(get("/user/dashboard").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
        mockMvc.perform(get("/admin/dashboard").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
    }

    @Test
    void lesMotsDePasseSontHaches() {
        assertTrue(userDetailsService.loadUserByUsername("user").getPassword().startsWith("$2"));
    }

    @Test
    void uneRequetePostSansJetonCsrfEstRefusee() throws Exception {
        mockMvc.perform(post("/user/demandes").with(user("user").roles("USER"))
                        .param("objet", "O").param("description", "D"))
                .andExpect(status().isForbidden());
    }

    @Test
    void chaqueUtilisateurNeVoitQueSesDemandes() throws Exception {
        mockMvc.perform(post("/user/demandes").with(csrf()).with(user("sara").roles("USER"))
                        .param("objet", "Demande de Sara").param("description", "Texte"))
                .andExpect(redirectedUrl("/user/dashboard"));

        mockMvc.perform(get("/user/dashboard").with(user("sara").roles("USER")))
                .andExpect(content().string(containsString("Demande de Sara")));
        mockMvc.perform(get("/user/dashboard").with(user("omar").roles("USER")))
                .andExpect(content().string(not(containsString("Demande de Sara"))));
        mockMvc.perform(get("/admin/dashboard").with(user("admin").roles("ADMIN")))
                .andExpect(content().string(containsString("Demande de Sara")));
    }

    @Test
    void uneDemandeSansObjetNEstPasCreee() throws Exception {
        mockMvc.perform(post("/user/demandes").with(csrf()).with(user("vide").roles("USER"))
                        .param("objet", " ").param("description", "Texte perdu"))
                .andExpect(redirectedUrl("/user/dashboard"));

        mockMvc.perform(get("/user/dashboard").with(user("vide").roles("USER")))
                .andExpect(content().string(not(containsString("Texte perdu"))));
    }

    @Test
    void lAdministrateurRepondEtLUtilisateurLitLaReponse() throws Exception {
        mockMvc.perform(post("/user/demandes").with(csrf()).with(user("lina").roles("USER"))
                        .param("objet", "Mot de passe oublié").param("description", "Aide"))
                .andExpect(redirectedUrl("/user/dashboard"));

        String page = mockMvc.perform(get("/admin/dashboard").with(user("admin").roles("ADMIN")))
                .andReturn().getResponse().getContentAsString();
        String debut = page.substring(0, page.indexOf("Mot de passe oublié"));
        String suite = page.substring(debut.length());
        String id = suite.replaceFirst("(?s).*?/admin/demandes/(\\d+)/reponses.*", "$1");

        mockMvc.perform(post("/admin/demandes/" + id + "/reponses").with(csrf()).with(user("user").roles("USER"))
                        .param("message", "Refusé"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/admin/demandes/" + id + "/reponses").with(csrf()).with(user("admin").roles("ADMIN"))
                        .param("message", "Compte réinitialisé"))
                .andExpect(redirectedUrl("/admin/dashboard"));

        mockMvc.perform(get("/user/dashboard").with(user("lina").roles("USER")))
                .andExpect(content().string(containsString("Compte réinitialisé")))
                .andExpect(content().string(containsString("Traitée")));
    }

    @Test
    void repondreAUneDemandeInconnueRenvoie404() throws Exception {
        mockMvc.perform(post("/admin/demandes/999999/reponses").with(csrf()).with(user("admin").roles("ADMIN"))
                        .param("message", "Texte"))
                .andExpect(status().isNotFound());
    }

    @Test
    void leContenuDesDemandesEstEchappe() throws Exception {
        mockMvc.perform(post("/user/demandes").with(csrf()).with(user("xss").roles("USER"))
                        .param("objet", "<script>alert(1)</script>").param("description", "Texte"))
                .andExpect(redirectedUrl("/user/dashboard"));

        mockMvc.perform(get("/user/dashboard").with(user("xss").roles("USER")))
                .andExpect(content().string(containsString("&lt;script&gt;")));
    }
}
