package net.arville.easybill.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseStructure {

    StatusStructure status;

    OutputStructure<?> output;

}
