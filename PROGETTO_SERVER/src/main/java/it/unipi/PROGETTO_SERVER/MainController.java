package it.unipi.PROGETTO_SERVER;

import com.google.gson.Gson;
import it.unipi.PROGETTO_SERVER.TabelleDB.Dipendente;
import it.unipi.PROGETTO_SERVER.Repositories.DipendenteRepository;
import it.unipi.PROGETTO_SERVER.Repositories.SedeLogisticaRepository;
import it.unipi.PROGETTO_SERVER.Repositories.VeicoloRepository;
import it.unipi.PROGETTO_SERVER.Repositories.ViaggioIdRepository;
import it.unipi.PROGETTO_SERVER.Repositories.ViaggioRepository;
import it.unipi.PROGETTO_SERVER.TabelleDB.SedeLogistica;
import it.unipi.PROGETTO_SERVER.TabelleDB.Veicolo;
import it.unipi.PROGETTO_SERVER.TabelleDB.Viaggio;
import it.unipi.PROGETTO_SERVER.TabelleDB.ViaggioId;
import it.unipi.PROGETTO_SERVER.TabelleDB.ViaggioSenzaCarico;
import it.unipi.PROGETTO_SERVER.otherClasses.BadRequestException;
import it.unipi.PROGETTO_SERVER.otherClasses.DynamicMessage;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "616157")
public class MainController {

    private static final Logger log = LogManager.getLogger(ProgettoServerApplication.class);
    @Autowired
    private DipendenteRepository dipendenteRepository;
    @Autowired
    private ViaggioRepository viaggioRepository;
    @Autowired
    private ViaggioIdRepository viaggioIdRepository;
    @Autowired
    private VeicoloRepository veicoloRepository;
    @Autowired
    private SedeLogisticaRepository sedeRepository;

    private static String generateToken() {  //funzione per la generazione di stringhe randomiche
        final int LEN = 32;
        String caratteri = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder token = new StringBuilder();
        while (token.length() < LEN) {
            int index = (int) (Math.random() * caratteri.length());
            token.append(caratteri.charAt(index));
        }
        return token.toString();
    }

    private Dipendente verificaCredenziali(String user, String token, boolean adminOnly) throws BadRequestException {
        if (user == null || token == null) {
            throw new BadRequestException("Compila tutti i campi");
        }
        Optional<Dipendente> res = dipendenteRepository.findById(user);
        if (res.isEmpty()) {
            throw new BadRequestException("Utente non registrato");  // comunichiamo che l'utente non è ancora registrato
        }
        Dipendente dip = res.get();
        if ((adminOnly && dip.getRuolo().equals("worker")) || dip.getSessionToken().equals(token) == false) {
            throw new BadRequestException("Accesso negato");
        }
        return dip;
    }

    private void logNotify(HttpServletRequest r, String[] args) {
        String tot = "( ";
        for (String s : args) {
            tot += (s + ",");
        }
        log.info(r.getRequestURI() + ": " + tot + ")");
    }

    private String decodeUsername(String s) throws BadRequestException {
        String res;
        try {
            res = URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException("Username non valido");
        }
        return res;
    }

    @PostMapping(path = "/register")
    public @ResponseBody
    void register(@RequestBody DynamicMessage r, HttpServletRequest q) throws BadRequestException { // (username, password, ruolo)
        String[] args = r.getStrings();
        logNotify(q, args);
        if (r.hasEmptyValue(3)) {
            throw new BadRequestException("Dati inseriti non validi");
        }

        Iterable<Dipendente> list = dipendenteRepository.findAll();
        for (Dipendente d : list) // se l'utente esiste già non puoi registrarti
        {
            if (d.getUsername().equals(args[0])) {
                throw new BadRequestException("L'username scelto esiste già");
            }
        }

        dipendenteRepository.save(new Dipendente(args[0], args[1], args[2], ""));  // la password è già hashata, possiamo direttamente salvare
    }

    @PostMapping(path = "/login")
    public @ResponseBody
    DynamicMessage login(@RequestBody DynamicMessage l, HttpServletRequest q) throws BadRequestException {   // (username, password)
        String args[] = l.getStrings();
        logNotify(q, args);

        if (l.hasEmptyValue(2)) {
            throw new BadRequestException("Dati inseriti non validi");
        }

        Optional<Dipendente> dip = dipendenteRepository.findById(args[0]);
        if (dip.isEmpty()) {
            throw new BadRequestException("Utente non registrato");  // comunichiamo che l'utente non è ancora registrato
        }
        Dipendente res = dip.get();
        if ((res.getUsername().equals(args[0]) && res.getPassword().equals(args[1])) == false) {
            throw new BadRequestException("Username o password errati"); // se le credenziali dell'elemento trovato non combaciano allora notificalo
        }
        String token = generateToken();     // generiamo un token di sessione
        dipendenteRepository.updateToken(args[0], token);   // updatiamolo
        String[] ret = {res.getRuolo(), token}; // rispondiamo con il ruolo dell'utente e con il suo token, in modo da poterli riutilizzare
        return new DynamicMessage(ret);
    }

    @PostMapping(path = "/users/{username}/view")
    public @ResponseBody
    ArrayList<DynamicMessage> view(@PathVariable("username") String username, @RequestBody DynamicMessage m, HttpServletRequest q) throws BadRequestException { // otteniamo implicitamente dal path il nome dell'utente, in modo da effettuare un controllo sugli accessi
        username = decodeUsername(username);
        String[] args = m.getStrings(); //(token, month, year)
        logNotify(q, args);

        Dipendente dip = verificaCredenziali(username, args[0], false);

        if (m.hasEmptyValue(3)) {
            throw new BadRequestException("Compila tutti i campi");
        }

        int mese = Integer.parseInt(args[1]);
        int anno = Integer.parseInt(args[2]);

        Iterable<ViaggioSenzaCarico> result;
        if (dip.getRuolo().equals("admin")) {
            result = viaggioIdRepository.monthCalendar(mese, anno); // se sei un admin ti vengono forniti tutti i viaggi
        } else {
            result = viaggioIdRepository.monthByUserId(dip.getUsername(), mese, anno);  // se sei un semplice lavoratore vengono solo restituiti i tuoi
        }
        ArrayList<DynamicMessage> dm = new ArrayList<>();   // ritorniamo i viaggi richiesti
        for (ViaggioSenzaCarico vsc : result) {
            dm.add(new DynamicMessage(vsc.getStrings()));    //(dipendente, veicolo, data, partenza, arrivo)
        }
        return dm;
    }

    @PostMapping(path = "/users/{username}/possibilities")
    public @ResponseBody
    ArrayList<DynamicMessage> possibilities(@PathVariable("username") String username, @RequestBody DynamicMessage m, HttpServletRequest q) throws BadRequestException { // otteniamo implicitamente dal path il nome dell'utente, in modo da effettuare un controllo sugli accessi
        username = decodeUsername(username);
        logNotify(q, m.getStrings());

        if (m.hasEmptyValue(1)) {
            throw new BadRequestException("Compila tutti i campi");  //(token)
        }
        verificaCredenziali(username, m.getStrings()[0], true);

        Iterable<Dipendente> dips = dipendenteRepository.findAll(); // otteniamo tutti i possibili valori di dipendenti, veicoli e sedi
        Iterable<Veicolo> vecs = veicoloRepository.findAll();
        Iterable<SedeLogistica> sedi = sedeRepository.findAll();
        ArrayList<DynamicMessage> res = new ArrayList<>();

        int size = ((Collection) dips).size();
        String[] str = new String[size];
        Iterator<Dipendente> itd = dips.iterator();
        for (int i = 0; i < size; i++) {
            str[i] = itd.next().getUsername();
        }
        res.add(new DynamicMessage(str));   // tutti i dipendenti

        size = ((Collection) vecs).size();
        str = new String[size];
        Iterator<Veicolo> itv = vecs.iterator();
        for (int i = 0; i < size; i++) {
            str[i] = itv.next().getNome();
        }
        res.add(new DynamicMessage(str));   // tutti i veicoli

        size = ((Collection) sedi).size();
        str = new String[size];
        Iterator<SedeLogistica> its = sedi.iterator();
        for (int i = 0; i < size; i++) {
            str[i] = its.next().getNome();
        }
        res.add(new DynamicMessage(str));   // tutte le sedi

        return res; // un ArrayList di tre DynamicMessage in uscita
    }

    @PostMapping(path = "/users/{username}/insert")
    public @ResponseBody
    void insert(@PathVariable("username") String username, @RequestBody DynamicMessage m, HttpServletRequest q) throws BadRequestException { // otteniamo implicitamente dal path il nome dell'utente, in modo da effettuare un controllo sugli accessi
        username = decodeUsername(username);
        String[] args = m.getStrings();
        logNotify(q, args);

        verificaCredenziali(username, args[0], true);  // solo il token nella richiesta

        if (m.hasEmptyValue(3)) {
            throw new BadRequestException("Compila tutti i campi");     // se args[2] è non nullo allora non lo è nemmeno args[1]
        }
        boolean res = true;
        boolean exits = false;
        switch (args[1]) {
            case "veicolo":
                if (veicoloRepository.existsById(args[2])) {
                    exits = true;
                    break;
                }
                if (veicoloRepository.save(new Veicolo(args[2])) == null) {
                    res = false;
                }
                break;
            case "sedelogistica":
                if (sedeRepository.existsById(args[2])) {
                    exits = true;
                    break;
                }
                if (sedeRepository.save(new SedeLogistica(args[2])) == null) {
                    res = false;
                }
                break;
            case "viaggio": //(token, tabella, dipendente, veicolo, data, partenza, arrivo, carico)
                if (m.hasEmptyValue(7)) {
                    throw new BadRequestException("Compila tutti i campi");     // ricontrolliamo che i campi siano tutti non vuoti
                }
                ViaggioId vi = new ViaggioId(args[2], args[3], LocalDate.parse(args[4]), args[5], args[6]);
                if (viaggioIdRepository.existsById(vi)) {
                    exits = true;
                    break;
                }
                try {
                    if (viaggioRepository.save(new Viaggio(vi, args[7].getBytes("UTF-8"))) == null) {
                        res = false;
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new BadRequestException("Impossibile memorizzare il file");
                }
                break;
            default:
                throw new BadRequestException("La tabella richiesta non esiste");
        }
        if (res == false) {
            throw new BadRequestException("Inserimento non riuscito");
        }
        if (exits == true) {
            throw new BadRequestException("Il dato inserito esiste già");
        }
    }

    @PostMapping(path = "/users/{username}/download")
    public @ResponseBody
    String download(@PathVariable("username") String username, @RequestBody DynamicMessage m, HttpServletRequest q) throws BadRequestException { // otteniamo implicitamente dal path il nome dell'utente, in modo da effettuare un controllo sugli accessi
        username = decodeUsername(username);
        String[] args = m.getStrings();
        logNotify(q, args);

        verificaCredenziali(username, args[0], false);
        if (m.hasEmptyValue(6)) {
            throw new BadRequestException("Compila tutti i campi");
        }

        Optional<Viaggio> res = viaggioRepository.findById(new ViaggioId(args[1], args[2], LocalDate.parse(args[3]), args[4], args[5]));
        if (res.isEmpty()) {
            throw new BadRequestException("Il viaggio non esiste");
        }
        Viaggio v = res.get();
        String carico = null;
        try {
            carico = new String(v.getCarico(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException("Impossibile ottenere il file");
        }
        return new Gson().toJson(carico);
    }

    @PostMapping(path = "/users/{username}/remove")
    public @ResponseBody
    void remove(@PathVariable("username") String username, @RequestBody DynamicMessage m, HttpServletRequest q) throws BadRequestException { // otteniamo implicitamente dal path il nome dell'utente, in modo da effettuare un controllo sugli accessi
        username = decodeUsername(username);
        String[] args = m.getStrings(); //(toke, usr, veicolo, data, part, arr)(data)
        logNotify(q, args);

        verificaCredenziali(username, args[0], true);
        if (m.hasEmptyValue(6)) {
            throw new BadRequestException("Compila tutti i campi");
        }

        viaggioRepository.deleteById(new ViaggioId(args[1], args[2], LocalDate.parse(args[3]), args[4], args[5]));
    }
}
