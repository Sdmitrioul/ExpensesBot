package com.dskroba.statemachine.state;

import com.dskroba.statemachine.effects.MessageEffect;
import com.dskroba.statemachine.effects.SelectEffect;
import com.dskroba.statemachine.state.descriptors.CommandStatesDescriptors;
import com.dskroba.type.SelectOption;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateContextConverterTest {
    private StateContextConverter converter = new StateContextConverter();

    @Test
    void basicTest() {
        StateContext context = new StateContext(90L);
        context.setState(State.requiring(CommandStatesDescriptors.MENU,
                SelectEffect.of("Choose menu operation", List.of(
                        SelectOption.base("Expenses"),
                        SelectOption.base("Statistic"),
                        SelectOption.base("Data"))).composite(new MessageEffect("Bla bla"))));
        context.setAttribute("Key", "Value");
        String serialize = converter.convertToString(context);
        StateContext deserialized = converter.parse(serialize);
        assertEquals(context, deserialized);
    }
}