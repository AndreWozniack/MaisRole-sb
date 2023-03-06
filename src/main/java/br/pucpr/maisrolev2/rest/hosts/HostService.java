package br.pucpr.maisrolev2.rest.hosts;


import br.pucpr.maisrolev2.lib.exception.AlreadyExistsException;
import br.pucpr.maisrolev2.lib.exception.NotFoundException;
import br.pucpr.maisrolev2.lib.exception.UnauthorizedException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HostService {
    private final HostRepository hostRepository;


    public HostService(HostRepository repository) {this.hostRepository = repository;}

    public Host add(Host host) {
        if (hostRepository.findHostByContact_Email(host.getContact().getEmail()).isEmpty()) {
            return hostRepository.save(host);
        }
        else throw new AlreadyExistsException("Username or email already taken.");
    }

    public Host getHost(Long id) {
        return hostRepository.findById(id).orElseThrow(() -> new NotFoundException("Host not found:" + id));
    }

    public List<Host> getAllHosts() {
        List<Host> hosts = hostRepository.findAll();
        if (hosts.isEmpty()) throw new NotFoundException("No hosts registered");
        return hosts;
    }
    public Host logHost(String email, String password) {
        var host = hostRepository.findHostByContact_Email(email);
        if (host.isPresent()) {
            var found = host.get();
            if (found.getPassword().equals(password)) {
                return found;
            }
            throw new UnauthorizedException("Incorrect email or password");
        }
        throw new NotFoundException("Incorrect email or password");
    }

}
