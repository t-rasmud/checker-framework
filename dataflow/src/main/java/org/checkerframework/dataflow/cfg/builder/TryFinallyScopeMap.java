package org.checkerframework.dataflow.cfg.builder;

import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.Name;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A map that keeps track of new labels added within a try block. For names that are outside of the
 * try block, the finally label is returned. This ensures that a finally block is executed when
 * control flows outside of the try block.
 */
@SuppressWarnings("serial")
class TryFinallyScopeMap extends HashMap<Name, Label> {
    /** New labels within a try block that were added by this implementation. */
    private final @Det Map<Name, Label> accessedNames;

    /** Create a new TryFinallyScopeMap. */
    // true positive; `accessedNames` is declared as a Det Map but assigned an OrderNonDet HashMap
    // Fixed: https://github.com/typetools/checker-framework/commit/3057728a41b1dc1d53eaa2ffeb1bb1e9ec303e57
    protected @OrderNonDet TryFinallyScopeMap() {
        this.accessedNames = new HashMap<>();
    }

    @Override
    @SuppressWarnings("determinism") // imprecise field access rule: accessing @OrderNonDet field of
    // @PolyDet receiver gives @NonDet, should be @PolyDet("upDet")
    public Label get(@PolyDet TryFinallyScopeMap this, @PolyDet @Nullable Object key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (super.containsKey(key)) {
            return super.get(key);
        } else {
            if (accessedNames.containsKey(key)) {
                return accessedNames.get(key);
            }
            Label l = new Label();
            accessedNames.put((Name) key, l);
            return l;
        }
    }

    @Override
    @SuppressWarnings(
            "keyfor:contracts.conditional.postcondition.not.satisfied") // get adds everything
    public @PolyDet("down") boolean containsKey(
            @Nullable @PolyDet TryFinallyScopeMap this, @PolyDet @Nullable Object key) {
        return true;
    }

    @SuppressWarnings("determinism") // imprecise field access rule: accessing @OrderNonDet field of
    // @PolyDet receiver gives @NonDet, should be @PolyDet("upDet")
    public Map<Name, Label> getAccessedNames(@PolyDet TryFinallyScopeMap this) {
        return accessedNames;
    }
}
