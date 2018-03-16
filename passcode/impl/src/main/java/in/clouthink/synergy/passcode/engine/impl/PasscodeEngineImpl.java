package in.clouthink.synergy.passcode.engine.impl;

import in.clouthink.synergy.passcode.engine.PasscodeEngine;
import in.clouthink.synergy.passcode.model.Passcode;
import in.clouthink.synergy.passcode.model.PasscodeMessage;
import in.clouthink.synergy.passcode.model.PasscodeRequest;
import in.clouthink.synergy.passcode.model.Passcodes;
import in.clouthink.synergy.passcode.repository.PasscodeRepository;
import in.clouthink.synergy.shared.security.algorithm.TOTPUtils;
import in.clouthink.daas.edm.Edms;
import in.clouthink.daas.security.token.spi.KeyGeneratorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author dz
 */
@Service
public class PasscodeEngineImpl implements PasscodeEngine {

	private static final Log logger = LogFactory.getLog(PasscodeEngineImpl.class);

	@Autowired
	private PasscodeRepository passcodeRepository;

	@Override
	public void handlePasscodeRequest(PasscodeRequest request) {
		String category = request.getCategory();

		if (Passcodes.REGISTER.equalsIgnoreCase(category) && Passcodes.FORGET_PASSWORD.equalsIgnoreCase(category)) {
			logger.warn(String.format("The PasscodeRequest[category=%s] is not supported", category));
			return;
		}

		Passcode passcode = passcodeRepository.findByTelephoneAndCategory(request.getTelephone(),
																		  request.getCategory());

		if (passcode == null) {
			passcode = new Passcode();
			passcode.setTelephone(request.getTelephone());
			passcode.setCategory(request.getCategory());
			passcode.setWhenToNew(System.currentTimeMillis() + 60 * 1000);
			passcode.setWhenToExpire(System.currentTimeMillis() + 10 * 60 * 1000);

			String secretKey = KeyGeneratorFactory.getInstance().generateBase32Key();
			String otp = TOTPUtils.getGeneratedValue(secretKey, 60 * 1000);
			passcode.setPasscode(otp);

			passcode = passcodeRepository.save(passcode);
		}
		else {
			if (passcode.getWhenToNew() > System.currentTimeMillis()) {
				//not match the time criteria to resend
				logger.warn("not match the time criteria to resend");
				return;
			}

			String secretKey = KeyGeneratorFactory.getInstance().generateBase32Key();
			String otp = TOTPUtils.getGeneratedValue(secretKey, 60 * 1000);
			passcode.setPasscode(otp);

			passcode.setWhenToNew(System.currentTimeMillis() + 60 * 1000);
			passcode.setWhenToExpire(System.currentTimeMillis() + 10 * 60 * 1000);

			passcode = passcodeRepository.save(passcode);
		}

		PasscodeMessage passcodeMessage = new PasscodeMessage();
		passcodeMessage.setTelephone(passcode.getTelephone());
		passcodeMessage.setPasscode(passcode.getPasscode());

		if (Passcodes.REGISTER.equalsIgnoreCase(category)) {
			passcodeMessage.setCategory(Passcodes.REGISTER);
			Edms.getEdm("sms").dispatch(Passcodes.REGISTER, passcodeMessage);
		}
		else if (Passcodes.FORGET_PASSWORD.equalsIgnoreCase(category)) {
			passcodeMessage.setCategory(Passcodes.FORGET_PASSWORD);
			Edms.getEdm("sms").dispatch(Passcodes.FORGET_PASSWORD, passcodeMessage);
		}

	}

}
