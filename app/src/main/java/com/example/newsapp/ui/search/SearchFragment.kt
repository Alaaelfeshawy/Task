package com.example.newsapp.ui.search

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.domain.model.home.Article
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSearchBinding
import com.example.newsapp.databinding.LatestNewsItemBinding
import com.example.newsapp.databinding.TopNewsItemBinding
import com.example.newsapp.model.home.ArticleModel
import com.example.newsapp.model.home.ArticleModelMapper
import com.example.newsapp.ui.base.BaseAdapter
import com.example.newsapp.ui.base.BaseFragment
import com.example.newsapp.ui.home.view_holder.LatestNewsViewHolder
import com.example.newsapp.ui.util.Util
import com.example.newsapp.ui.util.Util.checkIfExistAndUpdateUI
import com.example.newsapp.ui.util.Util.updateUI
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }
    private val adapter: BaseAdapter<ArticleModel, LatestNewsItemBinding> by lazy {
        BaseAdapter(R.layout.latest_news_item,{
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToNewsDetailsFragment(it))
        }){
            LatestNewsViewHolder(it , { model , binding->
               ArticleModelMapper.mapper.toDomain(model)?.let {
                    checkIfExistAndUpdateUI(it,binding ,
                        viewModel , requireContext())
                }
            }){ model , binding->
                 ArticleModelMapper.mapper.toDomain(model)?.let {
                    updateUI(it, binding,viewModel , requireContext())
                }
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_search

    override fun viewSetup() {
        _binding = viewDataBinding
        binding.searchRecyclerView.adapter = adapter
        binding.tryAgain.setOnClickListener {
            binding.noInternetLayout.visibility= View.GONE
            binding.search.visibility= View.VISIBLE
            binding.searchRecyclerView.visibility= View.VISIBLE
        }
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()){
                    viewModel.search(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    override fun viewModelSetup() {
        viewModel.searchResults.observe(viewLifecycleOwner){
            if (it.isNullOrEmpty()){
                binding.searchRecyclerView.visibility= View.GONE
                binding.emptyList.visibility= View.VISIBLE
            }else{
                binding.searchRecyclerView.visibility= View.VISIBLE
                binding.emptyList.visibility= View.GONE
                adapter.setDataList(it)
            }
        }
        viewModel.stateListener.loading.observe(viewLifecycleOwner){
            it?.let {
                if(it){
                    binding.noInternetLayout.visibility= View.GONE
                    binding.searchRecyclerView.visibility= View.GONE
                    binding.progressBar.visibility= View.VISIBLE
                }else{
                    binding.progressBar.visibility= View.GONE
                }
            }
        }
        viewModel.stateListener.errorMessage.observe(viewLifecycleOwner){
                message -> message?.let {
            makeToast(it, Toast.LENGTH_SHORT)
        }
        }
        viewModel.stateListener.success.observe(viewLifecycleOwner){
                message -> message?.let {
            makeToast(it, Toast.LENGTH_SHORT)
        }
        }
        viewModel.noInternet.observe(viewLifecycleOwner){
            it?.let {
                if(it){
                    binding.noInternetLayout.visibility= View.VISIBLE
                    binding.search.visibility= View.GONE
                    binding.searchRecyclerView.visibility= View.GONE
                }else{
                    binding.noInternetLayout.visibility= View.GONE
                    binding.search.visibility= View.VISIBLE
                    binding.searchRecyclerView.visibility= View.VISIBLE
                }
            }
        }
    }



}