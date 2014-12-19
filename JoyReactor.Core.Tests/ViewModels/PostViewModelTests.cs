﻿using JoyReactor.Core.ViewModels;
using NUnit.Framework;
using System.Collections.Generic;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace JoyReactor.Core.Tests.ViewModels
{
    [TestFixture]
    public class PostViewModelTests
    {
        PostViewModel viewmodel;

        [SetUp]
        public void SetUp()
        {
            TestExtensions.SetUp();
            viewmodel = new PostViewModel();
        }

        [Test]
        public async Task LoadPost861529Test()
        {
            int id = TestExtensions.CreatePostIdDatabase(ID.SiteParser.JoyReactor, "861529");

            Assert.IsFalse(viewmodel.IsBusy);
            viewmodel.Initialize(id).GetAwaiter();
            Assert.IsTrue(viewmodel.IsBusy);

            await Task.Delay(400);
            Assert.IsFalse(viewmodel.IsBusy);

            var poster = (PostViewModel.PosterViewModel)viewmodel.ViewModelParts[0];
            Assert.AreEqual("http://img0.joyreactor.cc/pics/post/-770859.jpeg", poster.Image);
            Assert.AreEqual(13, viewmodel.Comments().Count);

            // ==============================================
            Assert.IsTrue(viewmodel.Comments().All(s => !s.IsRoot));

            viewmodel.Comments()[0].NavigateCommand.Execute(null);
            await Task.Delay(300);
            Assert.AreEqual(3, viewmodel.Comments().Count);
            Assert.IsTrue(viewmodel.Comments()[0].IsRoot);
            Assert.IsTrue(viewmodel.Comments().Skip(1).All(s => !s.IsRoot));

            viewmodel.Comments()[1].NavigateCommand.Execute(null);
            await Task.Delay(300);
            Assert.AreEqual(2, viewmodel.Comments().Count);
            Assert.IsTrue(viewmodel.Comments()[0].IsRoot);
            Assert.IsTrue(viewmodel.Comments().Skip(1).All(s => !s.IsRoot));

            viewmodel.Comments()[0].NavigateCommand.Execute(null);
            await Task.Delay(300);
            Assert.AreEqual(3, viewmodel.Comments().Count);
            Assert.IsTrue(viewmodel.Comments()[0].IsRoot);
            Assert.IsTrue(viewmodel.Comments().Skip(1).All(s => !s.IsRoot));

            viewmodel.Comments()[0].NavigateCommand.Execute(null);
            await Task.Delay(300);
            Assert.AreEqual(13, viewmodel.Comments().Count);
            Assert.IsTrue(viewmodel.Comments().All(s => !s.IsRoot));
        }
    }

    static class PostViewModelExtensions
    {
        public static List<PostViewModel.CommentViewModel> Comments(this PostViewModel viewmodel)
        {
            return viewmodel.ViewModelParts.OfType<PostViewModel.CommentViewModel>().ToList();
        }
    }
}