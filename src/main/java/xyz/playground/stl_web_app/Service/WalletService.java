package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Model.Wallet;
import xyz.playground.stl_web_app.Repository.UserRepository;
import xyz.playground.stl_web_app.Repository.WalletRepository;

import java.math.BigDecimal;

import static xyz.playground.stl_web_app.Constants.StringConstants.ADMIN_ROLE;

@Service
public class WalletService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    public void createWallet(Long userId) {

        Wallet wallet = new Wallet();

        wallet.setOwnerId(userId);
        wallet.setBalance(new BigDecimal(0));

        walletRepository.save(wallet);
    }

    public BigDecimal getWalletBalance(Long ownerId) {
        return getWalletByOwnerId(ownerId).getBalance();
    }

    public Wallet getWalletByOwnerId(Long ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid owner id: " + ownerId));
    }

    public void decreaseAmount(Long ownerId, BigDecimal value) {

        //Get existing and active user
        User user = userRepository.findByIdAndEnabledTrue(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User id either inactive or not existing: " + ownerId));

        //Only decrease dispatcher wallet. Admin wallet are infinite
        if (!user.hasRole(ADMIN_ROLE)) {

            Wallet wallet = walletRepository.findByOwnerId(ownerId)
                    .orElseThrow(() -> new IllegalArgumentException("Wallet not found for owner: " + ownerId));

            BigDecimal newBalance = wallet.getBalance().subtract(value);

            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Insufficient funds");
            }

            wallet.setBalance(newBalance);
            walletRepository.save(wallet);
        }
    }

    public void increaseAmount(Long ownerId, BigDecimal value) {
        Wallet wallet = walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for owner: " + ownerId));

        wallet.setBalance(wallet.getBalance().add(value));
        walletRepository.save(wallet);
    }

    public void adjustWallets(Long requestedBy, Long requestedTo, BigDecimal amount) {
        //Decrease wallet first
        decreaseAmount(requestedTo, amount);
        //Increase wallet last
        increaseAmount(requestedBy, amount);
    }
}
