package net.arville.easybill.configuration;

import net.arville.easybill.model.User;
import net.arville.easybill.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DatabaseSeeder {
    @Bean
    public CommandLineRunner runner(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() > 0) return;
            var listUsers = List.of(
                    User.builder()
                            .username("adji")
                            .password("$2b$12$nnnVK0.5g6sdRpT2KHUdN.GjynUBCur7.eZfbze/h90gUxRoLuOfO")
                            .build(),
                    User.builder()
                            .username("alfredo")
                            .password("$2b$12$vknV0KNx/G.8kwqXE590U.hxHd4CEX10G1.KQc.8hZ1ANW2fyyDeS")
                            .build(),
                    User.builder()
                            .username("calista")
                            .password("$2b$12$K/TERxMvlALvr.dY8EOos.ptDpmkdCQRL8k/zxUXYTriu3rhJ4yKG")
                            .build(),
                    User.builder()
                            .username("daniel")
                            .password("$2b$12$MpSCtE8lN82mBvUkh2IScusPD4KMQgwYO6.e9a/st8/IrQzeMqcmm")
                            .build(),
                    User.builder()
                            .username("ethan")
                            .password("$2b$12$0NdiOw9pgAy0C/datN.FWeS9yD/PcVY8AiYfehFXmi5XW./si3xDi")
                            .build(),
                    User.builder()
                            .username("exaudina")
                            .password("$2b$12$6Z0tfK1X35Zhxt3bl6HI8uL8rp3NRs09LkzXzWPpHRXOTkcbg0Vm2")
                            .build(),
                    User.builder()
                            .username("felesia")
                            .password("$2b$12$FWU5CVPRQesV6AMw4Rka6.RWN0JaVovBJLjdUaQbpV6YXMS.1M8Dm")
                            .build(),
                    User.builder()
                            .username("fernando")
                            .password("$2b$12$NoT3/usvXiqpKMylzdRfHeUP28bZrZFe/RwFDBh1YT4VasSS4ovYW")
                            .build(),
                    User.builder()
                            .username("fransisca")
                            .password("$2b$12$1SzceQclgm34yrlJKjhfVOth4QvQAPOpZR5SMmx1X5t9b5KLPvYqS")
                            .build(),
                    User.builder()
                            .username("hirokhi")
                            .password("$2b$12$RYOK88.yVgXIMP.V9NabPekhBiktdtBR/BiLtQgTyUIZtndH.Qc1q")
                            .build(),
                    User.builder()
                            .username("ivanco")
                            .password("$2b$12$E9nz7959Rvn5tMk5GZ.eHuQ/iGN1sA8flR8ijg8FXgT.gh08TBWM6")
                            .build(),
                    User.builder()
                            .username("jason")
                            .password("$2b$12$lVFIez74zqgDNDfOiEUKo.uDmrHgd9y2zy1Ve1AarVdp4PiNNXREG")
                            .build(),
                    User.builder()
                            .username("jossen")
                            .password("$2b$12$qodfkWrPAvpxj28DaL9J4OSzKogFTb.S/95rMVBsCZ05uef2uF60q")
                            .build(),
                    User.builder()
                            .username("lusia")
                            .password("$2b$12$4MuzLDSVl0nYboTlsjIFMeCQmlHVzRWQ9O2468jbWYx2//fi1zZpK")
                            .build(),
                    User.builder()
                            .username("martin")
                            .password("$2b$12$yLRSoyA76AW2i20nXEgv9u4AMfky/N.8NMDGXbAA9VqZ46BvFVkjC")
                            .build(),
                    User.builder()
                            .username("michael")
                            .password("$2b$12$tsQewx4O49QVdO.ERT4CxeY663R5p5uUFVdE66s.hyakUKbo7A3rC")
                            .build(),
                    User.builder()
                            .username("michelle")
                            .password("$2b$12$aMmg8zDUUMbv144KVnjv4O8RtTuILn5zMvqzpJQAE.Y8savrGhTTu")
                            .build(),
                    User.builder()
                            .username("monica")
                            .password("$2b$12$oWUdDs60RTm.m03W07mlOeHbeX1rtcvzyfv3B4JaazwlqeSLVs3ri")
                            .build(),
                    User.builder()
                            .username("mulyani")
                            .password("$2b$12$BorkLHjSHnDiF0jXeTwp/O0o0RyVkxs36aTHu1jAlnBpY1WQLVTdu")
                            .build(),
                    User.builder()
                            .username("pascal")
                            .password("$2b$12$sFWopnFOW4/CTl8o3pkFFeH7mHj/uwNu65unN7sN4x.i2DXubSW1e")
                            .build(),
                    User.builder()
                            .username("rachmat")
                            .password("$2b$12$T1eeMmGh5OOrgTf0MZYnr.Zif.nhLaaNAchEIlvc.86Aa..NCOKWm")
                            .build(),
                    User.builder()
                            .username("ricky")
                            .password("$2b$12$AvUn3oAqoL0UWIzRbbJlGuhDpQNJGQMVOQWrEt02PzvAK4wreiOwi")
                            .build(),
                    User.builder()
                            .username("sharon")
                            .password("$2b$12$PyBAUJ94C6eXZs6/lcbgAOz/iuh8I1nncoHhiVmXbvAa9MWQdd/pC")
                            .build(),
                    User.builder()
                            .username("steven")
                            .password("$2b$12$esnjPoPfyVah7AQDaWFGmuu4z3O/bER0QcBEMRpL08lln6U.NjEXW")
                            .build(),
                    User.builder()
                            .username("vannessa")
                            .password("$2b$12$32b98GvI9D8n41zjfP4MDuv4Gej7YMhK7HQFX.NWHu6P1yUIPT3c2")
                            .build(),
                    User.builder()
                            .username("vincent")
                            .password("$2b$12$Jyn8AeuyzygEydXECggBUe3X3E3Vx/odg0UTs/gTFcTs6.k.BPzTK")
                            .build(),
                    User.builder()
                            .username("vincentius")
                            .password("$2b$12$Ufq.LaKBCsx6was0l1ziYe31vY/c660gioHR58cwFWpGMbC7UCbQS")
                            .build(),
                    User.builder()
                            .username("vincy")
                            .password("$2b$12$h8/9yHUk7OpzbHoLA5YKxOolorlhGvVe9ZdtQ.KvEcu7VaSwyTBiW")
                            .build(),
                    User.builder()
                            .username("winata")
                            .password("$2b$12$2Uco1huHR07qTDYEZt.fI.6Xypm03N5R0tQMrF0iQiUxBNchpc1ye")
                            .build(),
                    User.builder()
                            .username("yohanes")
                            .password("$2b$12$qO9EIsj1.JMEAa51XfmYnOGUMCQ20LrZpHEjzuS91/XEt/ZZjg0.G")
                            .build(),
                    User.builder()
                            .username("yovita")
                            .password("$2b$12$PxZfloM6zSP3g7hcCnQOl.8kC/wpmu5b9LllxVoOAy7IdKDCLm0V6")
                            .build()
            );

            userRepository.saveAll(listUsers);
        };

    }

}
