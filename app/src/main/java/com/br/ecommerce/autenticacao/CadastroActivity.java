package com.br.ecommerce.autenticacao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.br.ecommerce.databinding.ActivityCadastroBinding;
import com.br.ecommerce.helper.FirebaseHelper;
import com.br.ecommerce.model.Usuario;

public class CadastroActivity extends AppCompatActivity {
    private ActivityCadastroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();

    }

    public void validaDados(View view){
        String nome = binding.edtNome.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String telefone = binding.edtTelefone.getMasked();
        String senha = binding.edtSenha.getText().toString().trim();
        String confirmaSenha = binding.edtConfirmaSenha.getText().toString().trim();

        if(!nome.isEmpty()){
            if(!email.isEmpty()){
                if(!telefone.isEmpty()){
                    if(telefone.length() == 15){
                        if(!senha.isEmpty()){
                            if(!confirmaSenha.isEmpty()){
                                if(senha.equals(confirmaSenha)){

                                    binding.progressBar.setVisibility(View.VISIBLE);

                                    Usuario usuario = new Usuario();
                                    usuario.setNome(nome);
                                    usuario.setEmail(email);
                                    usuario.setTelefone(telefone);
                                    usuario.setSenha(senha);

                                    criarConta(usuario);

                                }else {
                                    binding.edtConfirmaSenha.requestFocus();
                                    binding.edtConfirmaSenha.setError("Senha n??o confere.");
                                }
                            }else {
                                binding.edtConfirmaSenha.requestFocus();
                                binding.edtConfirmaSenha.setError("Confirme sua senha.");
                            }
                        }else {
                            binding.edtSenha.requestFocus();
                            binding.edtSenha.setError("Informe uma senha.");
                        }
                    }else {
                        binding.edtTelefone.requestFocus();
                        binding.edtTelefone.setError("Fomato do telefone inv??lido.");
                    }
                }else {
                    binding.edtTelefone.requestFocus();
                    binding.edtTelefone.setError("Informe um n??mero de telefone.");
                }
            }else {
                binding.edtEmail.requestFocus();
                binding.edtEmail.setError("Informe seu email.");
            }
        }else {
            binding.edtNome.requestFocus();
            binding.edtNome.setError("Informe seu nome.");
        }
    }

    private void criarConta(Usuario usuario){
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String id = task.getResult().getUser().getUid();

                usuario.setId(id);
                usuario.salvar();

                Intent intent = new Intent();
                intent.putExtra("email", usuario.getEmail());
                setResult(RESULT_OK, intent);
                finish();

            }else {
                Toast.makeText(this, FirebaseHelper.validaErros(task.getException().getMessage()), Toast.LENGTH_SHORT).show();
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    private void configClicks() {
        binding.include.ibVoltar.setOnClickListener(view -> finish());
        binding.btnLogin.setOnClickListener(view -> finish());
    }
}