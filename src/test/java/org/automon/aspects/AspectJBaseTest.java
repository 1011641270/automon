package org.automon.aspects;

import org.aspectj.lang.Aspects;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.monitors.OpenMon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AspectJBaseTest {

    public static final RuntimeException  TEST_RUNTIME_EXCEPTION = new RuntimeException("testing throwing exceptions");
    private OpenMon openMon = mock(OpenMon.class);

    @Before
    public void setUp() throws Exception {
        MyAspectJTestAspect aspect = Aspects.aspectOf(MyAspectJTestAspect.class);
        aspect.setOpenMon(openMon);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSys_monitor() throws Exception {
        MyTestClass obj = new MyTestClass();
        obj.hello();
        obj.world();
        obj.iAmHiding();

        // start/stop pair should be called once per public method due to public method pointcut.
        verify(openMon, times(2)).start(anyString());
        verify(openMon, times(2)).stop(any());
    }

    @Test
    public void testSys_exceptions() throws Exception {
        MyTestClass obj = new MyTestClass();
        try {
            obj.throwException();
        } catch (Throwable t) {
            assertThat(t).isEqualTo(TEST_RUNTIME_EXCEPTION);
        }

        verify(openMon).exception(anyString());
    }



    // Note the @Override annotation was not used below as it will not compile with ajc.
    @Aspect
    static class MyAspectJTestAspect extends AspectJBase {

        @Pointcut("within(MyTestClass) && org.automon.pointcuts.Select.publicMethod()")
        public void user_monitor() {

        }

        @Pointcut("within(MyTestClass) && org.automon.pointcuts.Select.publicMethod()")
        public void user_exceptions() {
        }

    }


    private class MyTestClass {
        public void hello() {

        }

        public void world() {

        }

        private void iAmHiding() {

        }

        public void throwException() {
            throw TEST_RUNTIME_EXCEPTION;
        }
    }
}