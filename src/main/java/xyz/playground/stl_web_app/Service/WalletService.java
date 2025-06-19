package xyz.playground.stl_web_app.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Model.Wallet;
import xyz.playground.stl_web_app.Repository.WalletRepository;

import java.math.BigDecimal;

import static xyz.playground.stl_web_app.Constants.StringConstants.ADMIN_ROLE;

@Service
public class WalletService {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletRepository walletRepository;

    public void createWallet(Long userId) {

        Wallet wallet = new Wallet();

        wallet.setOwnerId(userId);
        wallet.setBalance(new BigDecimal(0));

        walletRepository.save(wallet);
    }

    public Wallet getWalletByOwnerId(Long ownerId) {
        return walletRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid owner id: " + ownerId));
    }

    public BigDecimal getWalletBalance(Long ownerId) {
        return getWalletByOwnerId(ownerId).getBalance();
    }

    public void transferAmount(Long destinationId, Long sourceId, BigDecimal amount) {
        
        //Get existing and (source) active user for validation
        User sourceUser = userService.getActiveUser(sourceId);
        Wallet sourceWallet = getWalletByOwnerId(sourceUser.getId());
        BigDecimal sourceAmountOriginal = sourceWallet.getBalance();

        //Get existing and (destination) active user for validation
        User destinationUser = userService.getActiveUser(destinationId);
        Wallet destinationWallet = getWalletByOwnerId(destinationUser.getId());
        BigDecimal destinationAmountOriginal = destinationWallet.getBalance();

        try {
            //Only deduct from non-admin wallet
            if(!sourceUser.hasRole(ADMIN_ROLE)) {
                deductAmount(sourceWallet, amount);
            }

            //Only add to non-admin wallet
            if(!destinationUser.hasRole(ADMIN_ROLE)) {
                increaseAmount(destinationWallet, amount);
            }

        } catch (Exception e) {
            //If an error occurs. adjust wallet back to original state
            adjustWallet(sourceWallet, sourceAmountOriginal);
            adjustWallet(destinationWallet, destinationAmountOriginal);
            throw new IllegalArgumentException(e.getMessage());
        }

    }

    @Transactional
    public void adjustWallet(Wallet wallet, BigDecimal amount) {
        if (wallet.getId() != null) {
            wallet.setBalance(amount);
            walletRepository.save(wallet);
        } else {
            throw new RuntimeException("Wallet does not exist: " + wallet.getId());
        }
    }

    public void deductWallet(Long ownerId, BigDecimal amount) {
        User user = userService.getActiveUser(ownerId);
        Wallet wallet = getWalletByOwnerId(user.getId());
        deductAmount(wallet, amount);
    }

    public void increaseWallet(Long ownerId, BigDecimal amount) {
        User user = userService.getActiveUser(ownerId);
        Wallet wallet = getWalletByOwnerId(user.getId());
        increaseAmount(wallet, amount);
    }

    @Transactional
    private void deductAmount(Wallet wallet, BigDecimal amount) {
        BigDecimal newBalance = wallet.getBalance().subtract(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        wallet.setBalance(newBalance);

        walletRepository.save(wallet);
    }

    @Transactional
    private void increaseAmount(Wallet wallet, BigDecimal amount) {
        BigDecimal newBalance = wallet.getBalance().add(amount);

        wallet.setBalance(newBalance);

        walletRepository.save(wallet);
    }
}
