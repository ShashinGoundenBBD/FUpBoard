package za.co.bbd.grad.fupboard.cli.navigation;

public sealed class NavResponse permits NavResponse.Exit, NavResponse.Back, NavResponse.Stay, NavResponse.Push, NavResponse.Replace {
    public static Exit exit() {
        return new Exit();
    }
    public static Back back() {
        return new Back();
    }
    public static Stay stay() {
        return new Stay();
    }
    public static Push push(NavState newState) {
        return new Push(newState);
    }
    public static Replace replace(NavState newState) {
        return new Replace(newState);
    }

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
    
    public static final class Replace extends NavResponse {
        private NavState newState;

        public Replace(NavState newState) {
            this.newState = newState;
        }

        public NavState getNewState() {
            return newState;
        }
    }
}
