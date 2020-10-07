import { Component, AfterViewInit, ElementRef, ViewChild, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';

import { LoginService } from 'app/core/login/login.service';
import { AccountService } from 'app/core/auth/account.service';
import { AuthServerProvider } from 'app/core/auth/auth-jwt.service';

@Component({
  selector: 'jhi-login-modal',
  templateUrl: './login.component.html',
})
export class LoginModalComponent implements AfterViewInit, OnInit {
  @ViewChild('username', { static: false })
  username?: ElementRef;

  authenticationError = false;
  authenticationServers: any = [];
  localServerDefault = 'Choose server';
  serverUrl: string = this.localServerDefault;
  serverSelected: any;
  isAuthenticationServerLogin = false;

  loginForm = this.fb.group({
    serverUrl: [''],
    username: [''],
    password: [''],
    rememberMe: [false],
  });

  constructor(
    private loginService: LoginService,
    private authServerProvider: AuthServerProvider,
    private router: Router,
    public activeModal: NgbActiveModal,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.authServerProvider.getAuthenticationServers().subscribe(response => {
      this.authenticationServers = response;
    });
  }

  ngAfterViewInit(): void {
    if (this.username) {
      this.username.nativeElement.focus();
    }
  }

  cancel(): void {
    this.authenticationError = false;
    this.loginForm.patchValue({
      username: '',
      password: '',
    });
    this.activeModal.dismiss('cancel');
  }

  login(): void {
    if (
      this.loginForm.get('serverUrl') &&
      this.loginForm.get('serverUrl')?.value !== '' &&
      this.loginForm.get('serverUrl')?.value !== this.localServerDefault
    ) {
      this.isAuthenticationServerLogin = true;
    } else {
      this.isAuthenticationServerLogin = false;
    }

    console.log('serverUrl' + this.loginForm.get('serverUrl')?.value);

    this.loginService
      .login({
        isAuthenticationServerLogin: this.isAuthenticationServerLogin,
        serverUrl: this.loginForm.get('serverUrl')!.value,
        username: this.loginForm.get('username')!.value,
        password: this.loginForm.get('password')!.value,
        rememberMe: this.loginForm.get('rememberMe')!.value,
      })
      .subscribe(
        () => {
          this.authenticationError = false;
          this.activeModal.close();
          if (
            this.router.url === '/account/register' ||
            this.router.url.startsWith('/account/activate') ||
            this.router.url.startsWith('/account/reset/')
          ) {
            this.router.navigate(['']);
          }
        },
        () => (this.authenticationError = true)
      );
  }

  register(): void {
    this.activeModal.dismiss('to state register');
    this.router.navigate(['/account/register']);
  }

  requestResetPassword(): void {
    this.activeModal.dismiss('to state requestReset');
    this.router.navigate(['/account/reset', 'request']);
  }

  // chooseServer(server: any): void{
  //   this.serverUrl = server.name;
  // }
}
