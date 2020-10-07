export class Login {
  constructor(
    public isAuthenticationServerLogin: boolean,
    public serverUrl: string,
    public username: string,
    public password: string,
    public rememberMe: boolean
  ) {}
}
