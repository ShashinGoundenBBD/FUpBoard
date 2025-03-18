package za.co.bbd.grad.fupboard.cli.navigation;

public sealed class NavResponse permits NavResponse.Exit, NavResponse.Back, NavResponse.Stay, NavResponse.Push {
    public static final class Exit extends NavResponse {
        public Exit() {}
    }
    
    public static final class Back extends NavResponse {
        public Back() {}
    }
    
    public static final class Stay extends NavResponse {
        public Stay() {}
    }
    
    public static final class Push extends NavResponse {
        private NavState newState;

        public Push(NavState newState) {
            this.newState = newState;
        }

        public NavState getNewState() {
            return newState;
        }
    }
}
