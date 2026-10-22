package co.edu.unicauca.lifecycle;

import co.edu.unicauca.lifecycle.model.QuestionState;
import co.edu.unicauca.lifecycle.model.QuestionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionStatusTest {

    @Test
    void flujoNormalDeRevision() {
        assertTrue(QuestionStatus.DRAFT.canTransitionTo(QuestionStatus.PENDING_REVIEW));
        assertTrue(QuestionStatus.PENDING_REVIEW.canTransitionTo(QuestionStatus.IN_REVIEW));
        assertTrue(QuestionStatus.IN_REVIEW.canTransitionTo(QuestionStatus.REJECTED));
        assertTrue(QuestionStatus.REJECTED.canTransitionTo(QuestionStatus.DRAFT));
    }

    @Test
    void noSePuedeAprobarUnaPreguntaEnDraft() {
        assertFalse(QuestionStatus.DRAFT.canTransitionTo(QuestionStatus.APPROVED));
    }

    @Test
    void noSePuedeEnviarDosVecesARevision() {
        assertFalse(QuestionStatus.PENDING_REVIEW.canTransitionTo(QuestionStatus.PENDING_REVIEW));
    }

    @Test
    void applyTransitionRechazaTransicionesInvalidas() {
        QuestionState state = new QuestionState(1L, "user:2");
        assertThrows(IllegalStateException.class,
                () -> state.applyTransition(QuestionStatus.APPROVED, "admin", null));
        state.applyTransition(QuestionStatus.PENDING_REVIEW, "user:2", "Enviada a revisión");
        assertEquals(QuestionStatus.PENDING_REVIEW, state.getCurrentState());
    }
}
