﻿using Android.App;
using Android.OS;
using Android.Views;
using Android.Widget;
using GalaSoft.MvvmLight.Helpers;
using JoyReactor.Core.ViewModels;
using JoyReactor.Android.App.Base;
using Messenger = GalaSoft.MvvmLight.Messaging.Messenger;

namespace JoyReactor.Android.App
{
	[Activity (Label = "@string/profile", ParentActivity = typeof(HomeActivity))]			
	public class ProfileActivity : BaseActivity
	{
		protected override void OnCreate (Bundle bundle)
		{
			base.OnCreate (bundle);
			SetContentViewForFragment ();
			SupportActionBar.SetDisplayHomeAsUpEnabled (true);
			if (bundle == null)
				SetRootFragment (new ProfileFragment ());

			Messenger.Default.Register<LoginViewModel.NavigateToProfileMessage> (
				this, m => SetRootFragment (new ProfileFragment ()));
			Messenger.Default.Register<ProfileViewModel.NavigateToLoginMessage> (
				this, m => SetRootFragment (new LoginFragment ()));
		}

		protected override void OnDestroy ()
		{
			base.OnDestroy ();
			Messenger.Default.Unregister (this);
		}

		public class ProfileFragment : BaseFragment
		{
			ProfileViewModel viewmodel;

			public async override void OnCreate (Bundle savedInstanceState)
			{
				base.OnCreate (savedInstanceState);
				RetainInstance = true;
				await (viewmodel = new ProfileViewModel ()).Initialize ();
			}

			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				var view = inflater.Inflate (Resource.Layout.fragment_profile, null);
				var progress = view.FindViewById (Resource.Id.progress);
				viewmodel
					.SetBinding (() => viewmodel.IsLoading, progress, () => progress.Visibility)
					.ConvertSourceToTarget (s => s ? ViewStates.Visible : ViewStates.Gone);
				var username = view.FindViewById<TextView> (Resource.Id.username);
				viewmodel.SetBinding (() => viewmodel.Username, username, () => username.Text);
				var rating = view.FindViewById<TextView> (Resource.Id.rating);
				viewmodel.SetBinding (() => viewmodel.Rating, rating, () => rating.Text);
				view.FindViewById (Resource.Id.logout).SetCommand ("Click", viewmodel.LogoutCommand);
				return view;
			}
		}

		public class LoginFragment : BaseFragment
		{
			LoginViewModel viewmodel;

			public override void OnCreate (Bundle savedInstanceState)
			{
				base.OnCreate (savedInstanceState);
				RetainInstance = true;
				viewmodel = new LoginViewModel ();
			}

			public override View OnCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			{
				var view = inflater.Inflate (Resource.Layout.fragment_login, null);
				view.FindViewById (Resource.Id.login).SetCommand ("Click", viewmodel.LoginCommand);
				var username = view.FindViewById<EditText> (Resource.Id.username);
				viewmodel.SetBinding (() => viewmodel.Username, username, () => username.Text, BindingMode.TwoWay);
				var password = view.FindViewById<EditText> (Resource.Id.password);
				viewmodel.SetBinding (() => viewmodel.Password, password, () => password.Text, BindingMode.TwoWay);
				var progress = view.FindViewById (Resource.Id.progress);
				viewmodel
					.SetBinding (() => viewmodel.IsBusy, progress, () => progress.Visibility)
					.ConvertSourceToTarget (s => s ? ViewStates.Visible : ViewStates.Gone);
				return view;
			}
		}
	}
}