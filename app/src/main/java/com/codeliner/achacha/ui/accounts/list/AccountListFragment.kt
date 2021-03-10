package com.codeliner.achacha.ui.accounts.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codeliner.achacha.R
import com.codeliner.achacha.databinding.FragmentAccountListBinding
import com.codeliner.achacha.mains.MainViewModel
import com.codeliner.achacha.utils.Const
import com.example.helpers.ui.AnimationManager
import com.example.helpers.ui.getHeaderHide
import com.example.helpers.ui.getHeaderShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class AccountListFragment: Fragment() {

    private lateinit var binding: FragmentAccountListBinding
    private val viewModel: AccountListViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    override fun onStop() {
        super.onStop()
        exitAnim()
    }

    private fun exitAnim() {
        context?.let {
            val duration = resources.getInteger(R.integer.animation_duration_short).toLong()
            val animHeaderHide = it.getHeaderHide().apply {
                this.duration = duration
            }

            // note. header
            binding.headerContainer.startAnimation(animHeaderHide)
            // note. header
            binding.headerDividerBottom.startAnimation(animHeaderHide)
            // note. body
        }
    }

    override fun onStart() {
        super.onStart()
        enterAnim()
    }

    private fun enterAnim() {
        context?.let {
            val duration = resources.getInteger(R.integer.animation_duration_short).toLong()
            val animHeaderShow = it.getHeaderShow().apply {
                this.duration = duration
            }

            // note. header
            binding.headerContainer.startAnimation(animHeaderShow)
            // note. header
            binding.headerDividerBottom.startAnimation(animHeaderShow)
            // note. body

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initialize(inflater)
        observe()

        return binding.root
    }

    private fun initialize(inflater: LayoutInflater) {
        binding = FragmentAccountListBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun observe() {
        observeFabs()
        viewModel.accounts.observe(viewLifecycleOwner, Observer {
            Timber.d("accounts updated: ${it.size}")
            for (account in it) Timber.v("account: $account")
        })

    }

    private fun observeFabs() {
        observeFabCreate()
        observeFabClear()
        observeFabTest()
    }

    private fun observeFabCreate() {
        // note. when clicked create fab button from main
        AccountListViewModel.onAccountCreate.observe(viewLifecycleOwner, Observer { started ->
            if (started) {
                // note. start anim
                viewModel.navigateToAccountCreateAnimation()
                // note. complete navigate to create action
                AccountListViewModel.accountCreateJobComplete()
            }
        })

        // note. navigate (ui)
        viewModel.onNavigateToAccountCreateAnimation.observe(viewLifecycleOwner, Observer { started ->
            if (started) {
                exitAnim()
            }
        })

        // note. navigate (feature)
        viewModel.onNavigateToAccountCreateJob.observe(viewLifecycleOwner, Observer { started ->
            if (started) {
                findNavController().navigate(AccountListFragmentDirections.actionAccountListFragmentToAccountCreateFragment())

                viewModel.navigateToAccountCreateJobComplete()
            }
        })
    }

    private fun observeFabClear() {
        AccountListViewModel.onAccountClear.observe(viewLifecycleOwner, Observer { started ->
            if (started) {

                viewModel.clearAccounts()

                AccountListViewModel.accountClearJobComplete()
            }
        })
    }

    private fun observeFabTest() {
        AccountListViewModel.onAccountTest.observe(viewLifecycleOwner, Observer { started ->
            if (started) {

                AccountListViewModel.accountTestJobComplete()
            }
        })
    }
}